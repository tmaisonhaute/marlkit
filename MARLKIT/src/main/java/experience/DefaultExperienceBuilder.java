package experience;

import agent.MLKAgent;
import environment.MLKEnvironment;
import environment.observation.Observation;
import reward.Reward;

public class DefaultExperienceBuilder implements ExperienceBuilder {

	@Override
	public Experience buildExperience(MLKEnvironment environment, MLKAgent agent, Reward reward) {
		Observation obs = environment.getObservation(agent);
		return new Experience(obs, environment.getAction(agent), reward);
	}

}
