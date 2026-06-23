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

public class BroadcastRelativeObservationPositions implements CommunicationModel {

    @Override
    public void communicate(MLKAgent agent) {
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
                new ObjectMessage<>(new ObservationPositionsValuesMessage(agent, senderPosition, obs));

        simuAgent.broadcast(message,
                simuAgent.getAgentsWithRole(simuAgent.getCommunity(), simuAgent.getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE));
    }

    @Override
    public void handleCommunication(MLKAgent agent, Mailbox mailbox) {
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

    private void addConvertedObservation(State2D state, ObservationPositionsValues target,
            Pair<Double, Double> receiverPosition, Pair<Double, Double> senderPosition,
            ObservationPositionsValues senderObservation) {

        Pair<Double, Double> senderRelativeToReceiver = state.relativePosition(receiverPosition, senderPosition);

        for (ObservationPositionValue obs : senderObservation.getListObs()) {
            Pair<Double, Double> observedRelativeToSender = obs.getPosition().toPair2D();

            double x = senderRelativeToReceiver.getFirst() + observedRelativeToSender.getFirst();
            double y = senderRelativeToReceiver.getSecond() + observedRelativeToSender.getSecond();

            target.addObservationPosition(new ObservationPositionValue(Tuple.fromPair(new Pair<>(x, y)), obs.getValue()));
        }
    }

    protected State2D getState2D(MLKAgent agent) {
        State state = agent.getMLKEnvironment().getState();

        if (!(state instanceof State2D state2D)) {
            throw new IllegalStateException("BroadcastRelativeObservationPositions requires State2D.");
        }

        return state2D;
    }
}