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
import agent.action.MappedJointAction;
import environment.observation.Observation;
import environment.reward.Reward;
import evaluation.Measure;
import evaluation.NoSystemEvaluation;
import evaluation.SystemEvaluator;
import learning.Experience;
import madkit.simulation.environment.Environment2D;
import rewardmodeling.ReactionEvent;
import rewardmodeling.RewardModel;
import util.Pair;
import util.grafana.LearningData;

/**
 * Standard implementation of a 2D multi-agent reinforcement learning environment.
 * Manages agent interactions, observations, rewards, and learning data collection.
 */
public abstract class EnvironmentStandard extends Environment2D implements MLKEnvironment {

	protected AgentsGroup agents;
	protected RewardModel rewardModel;
	protected SystemEvaluator systemEvaluator;
	private boolean logSetup = false;
	private final int EPISODES_BEFORE_LOG = 1_000;
	
	/**
	 * The learning data that can be collected during the simulation.
	 * It can be used to log agent rewards, and other statistics.
	 */
	private final LearningData learningData = new LearningData();

	private List<String> evaluationMeasureNames;
	
	private Map<MLKAgent, Observation> agentsObservations;
	private Map<MLKAgent, Action> agentsActions;
	private Map<MLKAgent, Experience> agentsExperiences;
	
	/**
	 * Creates a new environment with the specified dimensions and interaction method.
	 *
	 * @param width the width of the environment
	 * @param height the height of the environment
	 * @param rewardModel the agent rewardStructure
	 */
	protected EnvironmentStandard(int width, int height, RewardModel rewardModel) {
		super(width, height);
		this.rewardModel = rewardModel;
		this.systemEvaluator = new NoSystemEvaluation();
		this.evaluationMeasureNames = new ArrayList<>();
		agentsObservations = new HashMap<>();
		agentsActions = new HashMap<>();
		agentsExperiences = new HashMap<>();
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
	public void addAgent(MLKAgent agent) {
		agents.addAgent(agent);
	}

	/**
	 * Sets up the initial state of the environment. This method should be
	 * implemented by subclasses to define specific state configurations.
	 */
	protected abstract void setupState();

	/**
	 * Sets up the agents in the environment.
	 */
	protected abstract void setupAgents();
	
	@Override
	public void init() {
		setupState(); 
		setupAgents();
	}
	
	@Override
	public void reset() {
		getState().reset();
		setupAgents();
		setupState();
	}
	
	/**
	 * Processes the environment reaction to agent influences.
	 * Computes dynamics based on agent actions, calculates rewards, and stores the resulting
	 * experiences for agents to collect via {@link #getExperience(MLKAgent)}.
	 */
	@Override
	public void step(){
		
		//Reaction
		Map<MLKAgent, List<ReactionEvent>> result = dynamics(agentsActions);
		systemEvaluator.evaluate(result);
		
		//Reward computation
		Map<MLKAgent, Pair<Action, Reward>> rewards = rewardComputation(result);
		
		//Store experiences for agents to collect
		agentsExperiences = combineObsActReward(rewards, agentsObservations);
		collectAndLogLearningData(agentsExperiences);
	}
	/**
	 * {@inheritDoc}
	 * Delegates to the state to compute observations for all agents.
	 */
	@Override
	public void computeObservations() {
		agentsObservations = getState().getObservations();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void influence(MLKAgent agent, Action action) {
		agentsActions.put(agent, action);
	}
	
	/**
	 * Computes rewards for all agents based on the dynamics result.
	 * Applies the reward model to the reaction events and combines actions with rewards.
	 *
	 * @param result map of agents to their reaction events from dynamics
	 * @return map of agents to their action and computed reward
	 */
	protected Map<MLKAgent, Pair<Action, Reward>> rewardComputation(Map<MLKAgent, List<ReactionEvent>> result) {
		Map<MLKAgent, Reward> rewards = rewardModel.rewardFunctions(result);
		
		return combineActionReward(agentsActions, rewards);
	}
	
	/**
	 * Combines separate action and reward maps into a single map of action-reward pairs per agent.
	 *
	 * @param actions map of agents to their actions
	 * @param rewards map of agents to their rewards
	 * @return map of agents to their action-reward pairs
	 */
	private Map<MLKAgent, Pair<Action, Reward>> combineActionReward(Map<MLKAgent, Action> actions, 
																	Map<MLKAgent, Reward> rewards) {
		Map<MLKAgent, Pair<Action, Reward>> actionRewardMap = new HashMap<>();
		for (Map.Entry<MLKAgent, Action> entry : actions.entrySet()) {
			MLKAgent agent = entry.getKey();
			Action action = entry.getValue();
			Reward reward = rewards.get(agent);
			actionRewardMap.put(agent, new Pair<>(action, reward));
		}
		return actionRewardMap;
	}
	
	/**
	 * Collects, processes and logs learning data from agent experiences.
	 *
	 * This method ensures the log system is properly set up, then collects learning data
	 * from the experiences of all agents. Every EPISODES_BEFORE_LOG episodes, it generates a CSV log
	 * of the average rewards, saves it to the log file, and clears the episode data.
	 *
	 * @param experiences A map associating each agent with its experience for the current step
	 */
	private void collectAndLogLearningData(Map<MLKAgent, Experience> experiences) {
		checkLogSetup();
		collectLearningData(experiences);
	}
	
	@Override
	public void clearStepVariables() {
		agentsObservations.clear();
		agentsActions.clear();
		agentsExperiences.clear();
	}
	
	@Override
	public Experience getExperience(MLKAgent agent) {
		return agentsExperiences.get(agent);
	}
	
	@Override
	public Experience getExperienceJointAction(MLKAgent agent) {
		Experience originalExperience = agentsExperiences.get(agent);
		Map<MLKAgent, Action> allMappedActions = getAgentsActions();
		MappedJointAction jointAction = new MappedJointAction(allMappedActions);
		return new Experience(originalExperience.getInput(), jointAction, originalExperience.getReward());
	}
	
	@Override
	public void onEpisodeEnd() {
		systemEvaluator.onEpisodeEnd();
		List<Measure> measures = systemEvaluator.getEpisodeMeasures();
		collectEpisodeData(measures);
		
		finalizeEpisodeData();
		
		int epCount = learningData.getAverageEpisodesCount();
		if (epCount > 0 && epCount % EPISODES_BEFORE_LOG == 0) {
			String logMessage = generateLogCSV(learningData.getAverageEpisodesReward());
			saveLogCSV(logMessage);
			learningData.clearEpisodes();
		}
		
		systemEvaluator.reset();
	}

	/**
	 * Called when the environment ends, typically at the end of a simulation run.
	 * It checks if the log is set up, generates a CSV log of the average rewards,
	 * saves it, and generates a graph URL for visualization.
	 */
	@Override
	public void onEnd() {
		systemEvaluator.onSimulationEnd();
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
				evaluationMeasureNames = systemEvaluator.getMeasureNames();
				if (evaluationMeasureNames != null && !evaluationMeasureNames.isEmpty()) {
					agentsNames.addAll(evaluationMeasureNames);
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
			Map<String, Double> avgExtras = d.getSecond();
			for (MLKAgent agent: agents.getAgents()) {
				Double reward = avgReward.get(agent);
				if (reward == null) {
					reward = 0.0;
				}
				logMessage.append(reward)
						.append(",");
			}
			if (evaluationMeasureNames != null && !evaluationMeasureNames.isEmpty()) {
				for (String measureName : evaluationMeasureNames) {
					Double value = avgExtras.get(measureName);
					if (value == null) {
						value = 0.0;
					}
					logMessage.append(value).append(",");
				}
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
	 * Defines the environment dynamics based on agent actions.
	 * @param actions A map of each agent to their action.
	 * @return A map of each agent to their reaction events.
	 */
	public abstract Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions);



	/**
	 * Combines observations, actions, and rewards into experiences for each agent.
	 *
	 * @param actionRewardMap map of agents to their action/reward pairs
	 * @param observationMap map of agents to their observations
	 * @return map of agents to their complete experiences
	 */
	protected Map<MLKAgent, Experience> combineObsActReward(Map<MLKAgent, Pair<Action, Reward>> actionRewardMap,
	        Map<MLKAgent, Observation> observationMap) {
	    Map<MLKAgent, Experience> combinedMap = new HashMap<>();
	
	    for (Map.Entry<MLKAgent, Observation> entry : observationMap.entrySet()) {
	        MLKAgent agent = entry.getKey();
	        Observation observation = entry.getValue();
	        Pair<Action, Reward> actionRewardPair = actionRewardMap.get(agent);
	
	        if (actionRewardPair != null) {
	            Experience experience = new Experience(observation, actionRewardPair.getFirst(), actionRewardPair.getSecond());
	            combinedMap.put(agent, experience);
	        }
	    }
	    return combinedMap;
	}

	/**
	 * Merges two sets of observations for each agent.
	 * If both maps contain an observation for the same agent, they are combined using {@link Observation#add(Observation)}.
	 *
	 * @param observations1 the first set of observations
	 * @param observations2 the second set of observations
	 * @return a new merged map of observations
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

	/**
	 * Prints the current state of the environment to standard output.
	 */
	protected void printState() {
		getState().print();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Observation getObservation(MLKAgent agent) {
		return agentsObservations.get(agent);
	}
	
	/**
	 * Replaces the current observations map.
	 *
	 * @param agentsObs the new observations map
	 */
	protected void setAgentsObservations(Map<MLKAgent, Observation> agentsObs) {
		agentsObservations = agentsObs;
	}

	/**
	 * Returns the current observations map.
	 *
	 * @return map of agents to their observations
	 */
	protected Map<MLKAgent, Observation> getAgentsObservations() {
		return agentsObservations;
	}

	/**
	 * Replaces the current actions map.
	 *
	 * @param agentsActs the new actions map
	 */
	protected void setAgentsActions(Map<MLKAgent, Action> agentsActs) {
		agentsActions = agentsActs;
	}
	
	/**
	 * Returns the current actions map.
	 *
	 * @return map of agents to their actions
	 */
	@Override
	public Map<MLKAgent, Action> getAgentsActions() {
		return agentsActions;
	}
	
	@Override
	public Action getAction(MLKAgent agent) {
		return agentsActions.get(agent);
	}
	
	@Override
	public LearningData getLearningData() {
		return learningData;
	}

	@Override
	public void setSystemEvaluator(SystemEvaluator systemEvaluator) {
		this.systemEvaluator = systemEvaluator;
		this.evaluationMeasureNames = new ArrayList<>();
		logSetup = false;
	}

	@Override
	public SystemEvaluator getSystemEvaluator() {
		return systemEvaluator;
	}

	
}
