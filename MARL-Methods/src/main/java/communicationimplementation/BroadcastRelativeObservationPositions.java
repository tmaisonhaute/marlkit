package communicationimplementation;

import java.util.List;

import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.state.State;
import environment.state.State2D;
import madkit.kernel.Mailbox;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;
import util.Pair;
import util.Tuple;

/**
 * Communication model that broadcasts the agent's observation of positions and values to other agents, 
 * converting them to the receiver's frame of reference.
 * 
 * <p>
 * This model is intended for agents whose observations implement {@link ObservationPositionsValues}.
 * At each step, each agent broadcasts its current observation of positions and values. When receiving
 * observations from other agents, it converts them to its own frame of reference based on the relative 
 * positions of the sender and receiver.
 * </p>
 */
public class BroadcastRelativeObservationPositions implements CommunicationModel {

    @Override
    public void communicatePreInfluence(MLKAgent agent) {
        if (!(agent instanceof SimuAgent simuAgent)) {
            throw new IllegalArgumentException("agent is not instance of SimuAgent");
        }

        if (!(agent.getObservation() instanceof ObservationPositionsValues obs)) {
            return;
        }

        State2D state = getState2D(agent);
        Pair<Double, Double> senderPosition = state.getAgentPosition(agent);

        if (senderPosition == null) {
            return;
        }

        ObjectMessage<ObservationPositionsValuesMessage> message =
                new ObjectMessage<>(new ObservationPositionsValuesMessage(agent, senderPosition, obs.copy()));

        simuAgent.broadcast(message,
                simuAgent.getAgentsWithRole(simuAgent.getCommunity(), simuAgent.getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE));
    }

    @Override
    public void handleCommunicationPreInfluence(MLKAgent agent, Mailbox mailbox) {
        if (!(agent.getObservation() instanceof ObservationPositionsValues ownObs)) {
            return;
        }

        State2D state = getState2D(agent);
        Pair<Double, Double> receiverPosition = state.getAgentPosition(agent);

        if (receiverPosition == null) {
            return;
        }

        ObservationPositionsValues extendedObservation = ownObs;

        List<ObjectMessage<ObservationPositionsValuesMessage>> messages = mailbox.getAll(message ->
                message instanceof ObjectMessage<?> objectMessage
                        && objectMessage.getContent() instanceof ObservationPositionsValuesMessage);

        for (ObjectMessage<ObservationPositionsValuesMessage> message : messages) {
            ObservationPositionsValuesMessage content = message.getContent();

            if (content.sender().equals(agent)) {
                continue;
            }

            addConvertedObservation(state, extendedObservation, receiverPosition, content.senderPosition(), content.observation());
        }

        agent.setRegisteredObservation(extendedObservation);
    }

    /**
     * Adds the converted observations from the sender to the target observation, converting them to the receiver's frame of reference.
     * 
     * <p>
     * If the converted observation is already present in the target observation, it will not be added again.
     * </p>
     * 
     * <p>
     * The conversion is done by calculating the relative position of the sender with respect to the receiver, 
     * and then adding the observed positions from the sender to this relative position.
     * </p>
     * 
     * @param state the current state of the environment
     * @param target the target observation to which converted observations will be added
     * @param receiverPosition the position of the receiver agent
     * @param senderPosition the position of the sender agent
     * @param senderObservation the observation from the sender agent
     */
    private void addConvertedObservation(State2D state, ObservationPositionsValues target,
            Pair<Double, Double> receiverPosition, Pair<Double, Double> senderPosition,
            ObservationPositionsValues senderObservation) {

        Pair<Double, Double> senderRelativeToReceiver = state.relativePosition(receiverPosition, senderPosition);

        for (ObservationPositionValue obs : senderObservation.getListObs()) {
            Pair<Double, Double> observedRelativeToSender = obs.getPosition().toPair2D();

            double x = senderRelativeToReceiver.getFirst() + observedRelativeToSender.getFirst();
            double y = senderRelativeToReceiver.getSecond() + observedRelativeToSender.getSecond();
            
            
            ObservationPositionValue convertedObservation = new ObservationPositionValue(Tuple.fromPair(new Pair<>(x, y)), obs.getValue());
            if (!target.getListObs().contains(convertedObservation)) {
            	target.addObservationPosition(convertedObservation);
            }
        }
    }

    /**
     * Gets the current state of the environment as a State2D object.
     * @param agent the agent whose environment state is to be retrieved
     * @return the current state of the environment as a State2D object
     */
    protected State2D getState2D(MLKAgent agent) {
        State state = agent.getMLKEnvironment().getState();

        if (!(state instanceof State2D state2D)) {
            throw new IllegalStateException("BroadcastRelativeObservationPositions requires State2D.");
        }

        return state2D;
    }
}