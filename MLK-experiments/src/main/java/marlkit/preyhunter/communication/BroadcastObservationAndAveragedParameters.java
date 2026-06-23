package marlkit.preyhunter.communication;

import agent.MLKAgent;
import communication.CommunicationModel;
import communicationimplementation.BroadcastAveragedPolicyParameters;
import communicationimplementation.BroadcastRelativeObservationPositions;
import madkit.kernel.Mailbox;

/**
 * PreyHunter-specific communication model combining:
 * <ul>
 *   <li>relative observation broadcasting before influence;</li>
 *   <li>policy parameter averaging at the end of each episode.</li>
 * </ul>
 * <p>
 * This class is intentionally specific to the PreyHunter experiment and does not try to provide
 * a generic communication model composition mechanism.
 * </p>
 */
public class BroadcastObservationAndAveragedParameters implements CommunicationModel {

    private final BroadcastRelativeObservationPositions observationCommunication;
    private final BroadcastAveragedPolicyParameters parameterCommunication;

    /**
     * Creates a communication model combining relative observation broadcast and policy parameter averaging.
     *
     * @param receivedParametersWeight the weight assigned to averaged received parameters at the end of an episode
     */
    public BroadcastObservationAndAveragedParameters(double receivedParametersWeight) {
        this.observationCommunication = new BroadcastRelativeObservationPositions();
        this.parameterCommunication = new BroadcastAveragedPolicyParameters(receivedParametersWeight);
    }

    @Override
    public void communicatePreInfluence(MLKAgent agent) {
        observationCommunication.communicatePreInfluence(agent);
    }

    @Override
    public void handleCommunicationPreInfluence(MLKAgent agent, Mailbox mailbox) {
        observationCommunication.handleCommunicationPreInfluence(agent, mailbox);
    }

    @Override
    public void communicateEndEpisode(MLKAgent agent) {
        parameterCommunication.communicateEndEpisode(agent);
    }

    @Override
    public void handleCommunicationEndEpisode(MLKAgent agent, Mailbox mailbox) {
        parameterCommunication.handleCommunicationEndEpisode(agent, mailbox);
    }
}