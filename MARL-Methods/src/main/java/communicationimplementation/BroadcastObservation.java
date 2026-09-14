package communicationimplementation;

import java.util.List;

import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import communication.CommunicationModel;
import environment.observation.Observation;
import madkit.kernel.Mailbox;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;

/**
 * Communication model that broadcasts the agent's observation to other agents.
 * 
 * <p>
 * This model is intended for agents whose observations implement {@link Observation}. 
 * At each step, each agent broadcasts its current observation. When receiving
 * observations from other agents, it adds them to its own observation.
 * </p>
 */
public class BroadcastObservation implements CommunicationModel {

	@Override
	public void communicatePreInfluence(MLKAgent agent) {
		if (agent instanceof SimuAgent simuAgent) {
			Observation obs = agent.getObservation();
			ObjectMessage<Observation> messageObservation = new ObjectMessage<>(obs.copy());
			simuAgent.broadcast(messageObservation, 
					simuAgent.getAgentsWithRole(simuAgent.getCommunity(), simuAgent.getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE));
		} else {
			throw new IllegalArgumentException("agent is not instance of simuAgent");
		}

	}
	
	@Override
	public void handleCommunicationPreInfluence(MLKAgent agent, Mailbox mailbox) {
		Observation obs = agent.getObservation();
		List<ObjectMessage<Observation>> messagesObservations = mailbox.getAll(message -> message instanceof ObjectMessage<?> objectMessage && objectMessage.getContent() instanceof Observation);
		Observation extendedObservation = obs;
		for (ObjectMessage<Observation> message : messagesObservations) {
			extendedObservation.add(message.getContent());
		}
		
		agent.setRegisteredObservation(extendedObservation);
	}
	 
	

}
