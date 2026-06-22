package communicationimplementation;

import java.util.List;

import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import environment.observation.Observation;
import madkit.kernel.Mailbox;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;

public class BroadcastObservation implements CommunicationModel {

	@Override
	public void communicate(MLKAgent agent) {
		if (agent instanceof SimuAgent simuAgent) {
			Observation obs = agent.getObservation();
			ObjectMessage<Observation> messageObservation = new ObjectMessage<>(obs);
			simuAgent.broadcast(messageObservation, 
					simuAgent.getAgentsWithRole(simuAgent.getCommunity(), simuAgent.getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE));
		} else {
			throw new IllegalArgumentException("agent is not instance of simuAgent");
		}

	}
	
	@Override
	public void handleCommunication(MLKAgent agent, Mailbox mailbox) {
		Observation obs = agent.getObservation();
		List<ObjectMessage<Observation>> messagesObservations = mailbox.getAll(message -> message instanceof ObjectMessage<?> objectMessage && objectMessage.getContent() instanceof Observation);
		Observation extendedObservation = obs;
		for (ObjectMessage<Observation> message : messagesObservations) {
			extendedObservation = extendedObservation.add(message.getContent());
		}
		
		agent.setRegisteredObservation(extendedObservation);
	}
	 

}
