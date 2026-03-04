package communicationimplementations;

import java.util.List;

import agent.MLKAgent;
import communication.CommunicationModule;
import environment.observation.Observation;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;

public class BroadcastObservation implements CommunicationModule {

	@Override
	public void communicate(MLKAgent agent) {
		if (agent instanceof SimuAgent simuAgent) {
			Observation obs = agent.getObservation();
			ObjectMessage<Observation> messageObservation = new ObjectMessage<>(obs);
			simuAgent.broadcast(messageObservation, 
					simuAgent.getAgentsWithRole(simuAgent.getCommunity(), simuAgent.getModelGroup(), "mlkagent"));
		}

	}
	
	@Override
	public Observation extendObservation(Observation observation, List<ObjectMessage<Observation>> messages) {
		Observation extendedObservation = observation;
		
		for (ObjectMessage<Observation> message : messages) {
			extendedObservation = extendedObservation.add(message.getContent());
		}
		
		return extendedObservation;
	}

}
