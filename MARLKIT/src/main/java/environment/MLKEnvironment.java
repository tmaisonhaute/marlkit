package environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import agent.MLKAgent;
import environment.observation.Observation;
import learning.Experience;
import simulation.LearningData;
import simulation.StepData;

/**
 * Represents an environment in which agents operate.
 */
public interface MLKEnvironment {
	public LearningData learningData = new LearningData();

	public abstract void reset();
	
	public abstract void receiveAgentInfo(MLKAgent agent);
	
	public abstract Map<MLKAgent, Experience> step();

	void setupAgent(MLKAgent agent);

	/**
	 * Collect progress made by agents to make statistics.
	 * @return A map of merged observations.
	 */
	default void queryLearningData(int numEpisode, StepData stepData) {
		learningData.addStep(numEpisode, stepData);
	}
}
