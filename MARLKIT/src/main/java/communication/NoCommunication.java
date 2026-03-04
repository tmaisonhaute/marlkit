package communication;

import java.util.List;

import agent.MLKAgent;
import environment.observation.Observation;
import madkit.messages.ObjectMessage;

public class NoCommunication implements CommunicationModule {

	@Override
	public void communicate(MLKAgent agent) {
		//Do Nothing
	}

	@Override
	public Observation extendObservation(Observation observation, List<ObjectMessage<Observation>> messages) {
		return observation;
	}

}
