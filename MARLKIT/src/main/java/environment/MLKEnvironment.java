package environment;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import agent.MLKAgent;
import environment.state.State;
import learning.Experience;
import util.grafana.Extra;
import util.grafana.LearningData;
import util.grafana.StepData;

/**
 * Represents an environment in which agents operate.
 */
public interface MLKEnvironment {
	/**
	 * The learning data that can be collected during the simulation.
	 * It can be used to log agent rewards, and other statistics.
	 */
	public LearningData learningData = new LearningData();
	
	/**
	 * Initializes the environment, setting up the initial state and any necessary configurations. 
	 * This method is called once after every onActivation(). 
	 */
	public abstract void init();

	/**
	 * Resets the environment to its initial state.
	 */
	public abstract void reset();
	
	/**
	 * Executes one step in the environment, where agents observe, act, and receive rewards.
	 *
	 * @return a map of agents to their experiences from this step
	 */
	public abstract Map<MLKAgent, Experience> step();

	/**
	 * Sets up an agent in the environment.
	 *
	 * @param agent the agent to set up
	 */
	void addAgent(MLKAgent agent);
	
	/**
	 * Returns the current state of the environment.
	 *
	 * @return the environment state
	 */
	public State getState();

	public void onEpisodeEnd();
	
	/**
	 * Collect progress made by agents to make statistics.
	 * You can Override this method to add extra data to the learning data, by calling {@link LearningData#addStep(StepData)},
	 * and optionally adding extra data by using {@link StepData#StepData(Map, Optional)} and {@link Extra}
	 *
	 * @param experiences A map of agents and their corresponding experiences.
	 *                    Each experience contains the agent's action, observation, and reward.
	 */
	default void collectLearningData(Map<MLKAgent, Experience> experiences) {
		StepData stepData = new StepData(experiences);
        learningData.addStep(stepData);
    }

	/**
	 * Initializes the log file with the specified lines.
	 * Must be called before using {@link #writeLog(String) writeLog} to write to the log.
	 * It should be called only once, typically at the start of the simulation.
	 *
	 * @param rows the initial lines to write to the log file
	 * @throws IOException if a write error occurs
	 */
	default void initLogFile(List<String> rows) throws IOException {
		learningData.initLogfile(rows);
	}

	/**
	 * Writes a log message into the learning data.
	 * You must call {@link #initLogFile(List)} before using this method to initialize the log file.
	 * It is recommended to call this method once per X episodes, where X is a large number to avoid excessive opening
	 * and closing of the log file. You can use {@link LearningData#getAverageEpisodesCount()}
	 * to determine the number of episodes currently logged.
	 * After calling this method, you should call {@link LearningData#clearEpisodes()} to clear all currently
	 * stored episodes in {@link LearningData} and prepare for the next set of episodes to collect.
	 *
	 * @param message the message to write in the log
	 * @throws IOException if a write error occurs
	 */
	default void writeLog(String message) throws IOException {
		learningData.writeLog(message);
	}
}
