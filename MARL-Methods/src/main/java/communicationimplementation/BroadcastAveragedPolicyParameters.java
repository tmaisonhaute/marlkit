package communicationimplementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import learning.policies.Parameterized;
import madkit.kernel.Mailbox;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;

/**
 * Communication model that broadcasts and averages policy parameters at the end of an episode.
 * <p>
 * This model is intended for agents whose policies implement {@link Parameterized}. At the end
 * of an episode, each agent broadcasts a copy of its current policy parameters. When receiving
 * parameters from other agents, it updates its own parameters using a weighted average:
 * </p>
 *
 * <pre>
 * newParameters = ownParameters * (1 - receivedParametersWeight)
 *               + average(receivedParameters) * receivedParametersWeight
 * </pre>
 *
 * <p>
 * This allows homogeneous agents to softly synchronize their learned policies without sharing
 * raw experiences or mixing trajectories. It is especially useful for algorithms such as PPO,
 * where merging experiences from different agents into the same sequential batch may break the
 * temporal structure of returns.
 * </p>
 *
 * <p>
 * Only received parameter arrays with the same length as the local policy parameters are used.
 * This class therefore assumes that compatible agents use policies with the same parameter
 * structure and ordering.
 * </p>
 */
public class BroadcastAveragedPolicyParameters implements CommunicationModel {

    private final double receivedParametersWeight;

    /**
     * Creates a communication model that averages received policy parameters with local parameters.
     *
     * @param receivedParametersWeight the weight assigned to the average of received parameters.
     * Must be in {@code [0, 1]}. A value of {@code 0.2} means that the updated parameters will be
     * composed of {@code 80%} local parameters and {@code 20%} average received parameters.
     */
    public BroadcastAveragedPolicyParameters(double receivedParametersWeight) {
        if (receivedParametersWeight < 0.0 || receivedParametersWeight > 1.0) {
            throw new IllegalArgumentException("receivedParametersWeight must be in [0, 1].");
        }

        this.receivedParametersWeight = receivedParametersWeight;
    }

    /**
     * Broadcasts the agent policy parameters at the end of the episode.
     * <p>
     * If the agent policy does not implement {@link Parameterized}, nothing is sent.
     * </p>
     *
     * @param agent the communicating agent
     */
    @Override
    public void communicateEndEpisode(MLKAgent agent) {
        if (!(agent instanceof SimuAgent simuAgent)) {
            throw new IllegalArgumentException("agent is not instance of SimuAgent");
        }

        if (!(agent.getPolicy() instanceof Parameterized parameterizedPolicy)) {
            return;
        }

        double[] parameters = parameterizedPolicy.getParameters();

        if (parameters == null) {
            return;
        }

        ParametersMessage message = new ParametersMessage(agent, parameters.clone());
        simuAgent.broadcast(new ObjectMessage<>(message), 
        		simuAgent.getAgentsWithRole(simuAgent.getCommunity(), simuAgent.getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE));
    }

    /**
     * Handles received policy parameters and updates the local policy by weighted averaging.
     * <p>
     * Messages sent by the agent itself are ignored. Messages with incompatible parameter lengths
     * are also ignored.
     * </p>
     *
     * @param agent the receiving agent
     * @param mailbox the mailbox containing received messages
     */
    @Override
    public void handleCommunicationEndEpisode(MLKAgent agent, Mailbox mailbox) {
        if (!(agent.getPolicy() instanceof Parameterized parameterizedPolicy)) {
            return;
        }

        double[] ownParameters = parameterizedPolicy.getParameters();

        if (ownParameters == null) {
            return;
        }

        List<ObjectMessage<ParametersMessage>> messages = 
        		mailbox.getAll(message -> message instanceof ObjectMessage<?> objectMessage 
        				&& objectMessage.getContent() instanceof ParametersMessage);
        List<double[]> receivedParameters = new ArrayList<>();

        for (ObjectMessage<ParametersMessage> message : messages) {
            ParametersMessage content = message.getContent();

            if (content.sender() == agent) {
                continue;
            }

            double[] parameters = content.parameters();

            if (parameters != null && parameters.length == ownParameters.length) {
                receivedParameters.add(parameters);
            }
        }

        if (receivedParameters.isEmpty()) {
            return;
        }

        double[] averageReceivedParameters = average(receivedParameters, ownParameters.length);
        double[] mergedParameters = merge(ownParameters, averageReceivedParameters);
        parameterizedPolicy.setParameters(mergedParameters);
    }

    /**
     * Computes the element-wise average of a list of parameter arrays.
     *
     * @param parametersList the parameter arrays to average
     * @param length the expected parameter array length
     * @return the averaged parameters
     */
    private double[] average(List<double[]> parametersList, int length) {
        double[] average = new double[length];

        for (double[] parameters : parametersList) {
            for (int i = 0; i < length; i++) {
                average[i] += parameters[i];
            }
        }

        for (int i = 0; i < length; i++) {
            average[i] /= parametersList.size();
        }

        return average;
    }

    /**
     * Merges local parameters with averaged received parameters.
     *
     * @param ownParameters the local parameters
     * @param receivedAverageParameters the average of compatible received parameters
     * @return the merged parameters
     */
    private double[] merge(double[] ownParameters, double[] receivedAverageParameters) {
        double[] merged = new double[ownParameters.length];
        double ownParametersWeight = 1.0 - receivedParametersWeight;

        for (int i = 0; i < ownParameters.length; i++) {
            merged[i] = ownParameters[i] * ownParametersWeight + receivedAverageParameters[i] * receivedParametersWeight;
        }

        return merged;
    }

    /**
     * Message containing the sender and a flat copy of its policy parameters.
     *
     * @param sender the agent that sent the parameters
     * @param parameters the flat policy parameters
     */
    private record ParametersMessage(MLKAgent sender, double[] parameters) {

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (!(object instanceof ParametersMessage(MLKAgent snd, double[] params))) {
                return false;
            }

            return sender == snd && Arrays.equals(parameters, params);
        }

        @Override
        public int hashCode() {
            int result = System.identityHashCode(sender);
            result = 31 * result + Arrays.hashCode(parameters);
            return result;
        }

        @Override
        public String toString() {
            return "ParametersMessage[sender=" + sender + ", parameters=" + Arrays.toString(parameters) + "]";
        }
    }
}