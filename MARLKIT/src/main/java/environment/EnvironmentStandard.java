package environment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;
import environment.state.State;
import learning.Experience;
import madkit.simulation.environment.Environment2D;
import rewardmodeling.RewardModeling;
import util.Pair;

/**
 * Standard implementation of a 2D multi-agent reinforcement learning environment.
 * Manages agent interactions, observations, rewards, and learning data collection.
 */
public abstract class EnvironmentStandard extends Environment2D implements MLKEnvironment {

	protected AgentsGroup agents;
	protected RewardModeling rewardStructure;
	private boolean logSetup = false;
	private final int EPISODES_BEFORE_LOG = 1_000;
	
	public EnvironmentStandard(int width, int height) {
		this(width, height, null);
	}
	
	/**
	 * Creates a new environment with the specified dimensions and interaction method.
	 *
	 * @param width the width of the environment
	 * @param height the height of the environment
	 * @param rewardStructure the agent rewardStructure
	 */
	public EnvironmentStandard(int width, int height, RewardModeling rewardStructure) {
		super(width, height);
		this.rewardStructure = rewardStructure;
    }
	
	/**
	 * Called when the environment is activated in the simulation.
	 * Initializes the environment role and agent group.
	 */
	@Override
	protected void onActivation() {
        super.onActivation();
		requestRole(getCommunity(), getModelGroup(), "mlkenvironment");
		agents = new AgentsGroup();
	}

	/**
	 * Sets up an agent in the environment by adding it to the agents group.
	 */
	@Override
	public void setupAgent(MLKAgent agent) {
		agents.addAgent(agent);
	}

	/**
	 * Sets up the initial state of the environment. This method should be
	 * implemented by subclasses to define specific state configurations.
	 */
	protected abstract void setupState();

	/**
	 * Executes one environment step where all agents: 1.observe, 2.act, and 3.learn from the resulting experience.
	 *
	 * @return map of each agent to their experience for this step.
	 */
	public Map<MLKAgent, Experience> step(){
		Map<MLKAgent,Observation> AgentObservations = processSocialObservations();
		getLogger().info("AgentObservations: " + AgentObservations);
		Map<MLKAgent, Pair<Action, Reward>> stepResult = environmentStep(AgentObservations);
		Map<MLKAgent, Experience> experiences = feedExpToAgent(stepResult, AgentObservations);
		collectAndLogLearningData(experiences);
		return experiences;
	}

	/**
	 * Collects, processes and logs learning data from agent experiences.
	 *
	 * This method ensures the log system is properly set up, then collects learning data
	 * from the experiences of all agents. Every 1,000 episodes, it generates a CSV log
	 * of the average rewards, saves it to the log file, and clears the episode data.
	 *
	 * @param experiences A map associating each agent with its experience for the current step
	 */
	private void collectAndLogLearningData(Map<MLKAgent, Experience> experiences) {
		checkLogSetup();
		collectLearningData(experiences);
		int epCount = learningData.getAverageEpisodesCount();
		if (epCount % EPISODES_BEFORE_LOG == 0) {
			String logMessage = generateLogCSV(learningData.getAverageEpisodesReward());
			saveLogCSV(logMessage);
			learningData.clearEpisodes();
		}
	}

	/**
	 * Called when the environment ends, typically at the end of a simulation run.
	 * It checks if the log is set up, generates a CSV log of the average rewards,
	 * saves it, and generates a graph URL for visualization.
	 */
	@Override
	public void onEnd() {
		checkLogSetup();
		if (learningData.getAverageEpisodesCount() > 0) {
			String logMessage = generateLogCSV(learningData.getAverageEpisodesReward());
			saveLogCSV(logMessage);
			learningData.clearEpisodes();
		}
		getLogger().info("Environment ended.");
        try {
            String url = learningData.generateGraph("log_" + Instant.now().getEpochSecond(), true);
			getLogger().info("Graph URL: " + url);
        } catch (IOException | URISyntaxException e) {
            getLogger().info("Error generating graph: " + e.getMessage());
        }
		getLogger().info("Log file: " + learningData.getLogFilePath());
    }

	/**
	 * Initializes the log file with the names of all agents in the environment.
	 * This method should be called only once, typically at the start of the simulation.
	 */
	private void checkLogSetup() {
		if (!logSetup) {
			try {
				List<String> agentsNames = new ArrayList<>();
				for (MLKAgent agent : agents.getAgents()) {
					agentsNames.add(agent.toString());
				}
				initLogFile(agentsNames);
				logSetup = true;
			} catch (Exception e) {
				getLogger().severe("Error setting up log file: " + e.getMessage());
			}
		}
	}

	/**
	 * Generates a CSV log message from the average rewards of all agents.
	 * Each line corresponds to an episode, with rewards for each agent separated by commas.
	 *
	 * @param data List of pairs containing average rewards for agents and additional data
	 * @return A string representing the CSV formatted log message
	 */
	private String generateLogCSV(List<Pair<Map<MLKAgent, Double>, Map<String, Double>>> data) {
		StringBuilder logMessage = new StringBuilder();
		for (Pair<Map<MLKAgent, Double>, Map<String, Double>> d : data) {
			Map<MLKAgent, Double> avgReward = d.getFirst();
			for (MLKAgent agent: agents.getAgents()) {
				Double reward = avgReward.get(agent);
				if (reward == null) {
					reward = 0.0;
				}
				logMessage.append(reward)
						.append(",");
			}
			logMessage.setLength(logMessage.length() - 1);
			logMessage.append("\n");
		}
		return logMessage.toString();
	}

	/**
	 * Saves the log message to the log file.
	 * This method handles any exceptions that may occur during the writing process.
	 *
	 * @param logMessage The message to be logged
	 */
	private void saveLogCSV(String logMessage) {
		try {
			writeLog(logMessage);
		} catch (Exception e) {
			getLogger().severe("Error writing to log file: " + e.getMessage());
		}
	}

	/**
	 * Process the social observations of each agent in 3 steps:
	 * for each agent:
	 * 1. observes environment state
	 * 2. Compute interaction information (ex: predict other agent next action)
	 * 3. Merges both into a final observation.
	 *
	 * @return a map of each agent to their complete observations.
	 */
	public Map<MLKAgent,Observation> processSocialObservations(){
		Map<MLKAgent, Observation> observations = getObservation();
		Map<MLKAgent, Observation> interactionInformations =  observations; //interactionMethod.getInteractionInformation(observations);
		getLogger().info("interactionInformations: " + interactionInformations);
		Map<MLKAgent, Observation> mergedObservations = mergeObservations(observations, interactionInformations);
		return mergedObservations;
	}

	/**
	 * Run one step in the environment based on agent observations:
	 * Each agent picks an action, the environment applies them, and returns rewards.
	 *
	 * @param observations a map of each agent to their observations
	 * @return a map of each agent to their action and reward
	 */
	public Map<MLKAgent, Pair<Action, Reward>> environmentStep(Map<MLKAgent, Observation> observations){
		Map<MLKAgent, Action> actions = agents.allAgentsTakeAction(observations);
		Map<MLKAgent, Pair<Action, Reward>> result = dynamics(actions);
		return result;
	}

	/**
	 * Process and sends experience data to each agent.
	 * For each agent, combine their observation, action, reward and send to him for learning.
	 *
	 * @param result map of agents to their Pair action/reward
	 * @param observations map of agent to their observation
	 * @return map of agents to their experience
	 */
	public Map<MLKAgent, Experience> feedExpToAgent(Map<MLKAgent, Pair<Action, Reward>> result, Map<MLKAgent,Observation> observations) {
		Map<MLKAgent, Experience> experiences = combineObsActReward(result, observations);
		sendFeedbackExperience(experiences);
		return experiences;
	}

	/**
	 * Get the current observations of all agents in the environment.
	 * @return A map of each agent to their observation.
	 */
	public Map<MLKAgent,Observation> getObservation() {
		return getState().getObservations();
	}
	
	/**
	 * Defines the environment dynamics based on agent actions.
	 * @param actions A map of each agent to their action.
	 * @return A map of each agent to their action and reward.
	 */
	public abstract Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions);
	
	protected void sendFeedbackExperience(Map<MLKAgent,Experience> experiences){
		for (Map.Entry<MLKAgent, Experience> entry : experiences.entrySet()) {
			MLKAgent agent = entry.getKey();
			Experience experience = entry.getValue();
			agent.feedbackExperience(experience);
		}
	}


	/**
	 * Combines observations, actions, and rewards into experiences for each agent.
	 * @param actionRewardMap
	 * @param observationMap
	 * @return
	 */
	protected Map<MLKAgent, Experience> combineObsActReward(Map<MLKAgent, Pair<Action, Reward>> actionRewardMap,
	        Map<MLKAgent, Observation> observationMap) {
	    Map<MLKAgent, Experience> combinedMap = new HashMap<>();
	
	    for (Map.Entry<MLKAgent, Observation> entry : observationMap.entrySet()) {
	        MLKAgent agent = entry.getKey();
	        Observation observation = entry.getValue();
	        Pair<Action, Reward> actionRewardPair = actionRewardMap.get(agent);
	
	        if (actionRewardPair != null) {
	            Experience experience = new Experience(
	                    observation,
	                    actionRewardPair.getFirst(),
	                    actionRewardPair.getSecond()
	            );
	            combinedMap.put(agent, experience);
	        }
	    }
	    return combinedMap;
	}

	/**
	 * Merges two sets of observations for each agent.
	 * @param observations1
	 * @param observations2
	 * @return
	 */
	protected Map<MLKAgent, Observation> mergeObservations(Map<MLKAgent, Observation> observations1,
			Map<MLKAgent, Observation> observations2) {
		if (observations1 == null) {
			return observations2 == null ? new HashMap<>() : new HashMap<>(observations2);
		}
		
		if (observations2 == null) {
			return new HashMap<>(observations1);
		}
		
		Map<MLKAgent, Observation> mergedObservations = new HashMap<>(observations1);
		for (Map.Entry<MLKAgent, Observation> entry : observations2.entrySet()) {
			MLKAgent agent = entry.getKey();
			Observation observation = entry.getValue();
			if (mergedObservations.containsKey(agent)) {
				Observation existingObservation = mergedObservations.get(agent);
				if (existingObservation == null) {
					mergedObservations.put(agent, observation);
				} else {
					mergedObservations.put(agent, existingObservation.add(observation));
				}
			}
			else {
				mergedObservations.put(agent, observation);
			}
		}
		return mergedObservations;
	}

	protected void printState() {
		getState().print();
	}
	
	/**
	 * Returns the current state of the environment.
	 *
	 * @return the environment state
	 */
	protected abstract State getState();
}
