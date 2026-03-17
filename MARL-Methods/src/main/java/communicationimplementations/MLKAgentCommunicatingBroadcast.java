package communicationimplementations;

import java.util.List;

import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import environment.observation.Observation;
import madkit.kernel.Mailbox;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;

public interface MLKAgentCommunicatingBroadcast extends MLKAgentCommunicating {

	@Override
	public default void communicate(){
		if (this instanceof SimuAgent simuAgent) {
			Observation obs = this.getObservation();
			ObjectMessage<Observation> messageObservation = new ObjectMessage<>(obs);
			simuAgent.broadcast(messageObservation, 
					simuAgent.getAgentsWithRole(simuAgent.getCommunity(), simuAgent.getModelGroup(), MLKAgent.DEFAULT_AGENT_ROLE));
		} else {
			throw new IllegalArgumentException("agent is not instance of simuAgent");
		}
	}
	
	@Override
	public default void handleCommunication(Mailbox mailbox) {
		Observation obs = this.getObservation();
		//TODO filter only observation messages
		List<ObjectMessage<Observation>> messagesObservations = mailbox.getAll(null);
		Observation extendedObservation = obs;
		for (ObjectMessage<Observation> message : messagesObservations) {
			extendedObservation = extendedObservation.add(message.getContent());
		}
		
		this.setRegisteredObservation(extendedObservation);
	}
}
