package experience;

import agent.MLKAgent;
import agent.action.Action;
import environment.MLKEnvironment;
import environment.observation.Observation;
import reward.Reward;

public class TransitionExperienceBuilder implements ExperienceBuilder {

	@Override
	public Experience buildExperience(MLKEnvironment environment, MLKAgent agent, Reward reward, boolean isTerminal) {
		Observation obs = environment.getObservation(agent);
		Action act = environment.getAction(agent);
		Observation nextObs = environment.getState().getObservations().get(agent);
		return new TransitionExperience(obs, act, reward, nextObs, isTerminal);
	}

}
