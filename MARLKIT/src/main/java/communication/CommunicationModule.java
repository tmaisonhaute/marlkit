package communication;

import java.util.List;

import agent.MLKAgent;
import environment.observation.Observation;
import madkit.messages.ObjectMessage;

public interface CommunicationModule {
	public void communicate(MLKAgent agent);
	public Observation extendObservation(Observation observation, List<ObjectMessage<Observation>> messages);
}
