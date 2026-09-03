package experience;

import agent.MLKAgent;
import environment.MLKEnvironment;
import reward.Reward;

public interface ExperienceBuilder {
	public Experience buildExperience(MLKEnvironment environment, MLKAgent agent, Reward reward, boolean isTerminal);
	
}
