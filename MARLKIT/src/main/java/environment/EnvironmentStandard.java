package environment;

import java.util.HashMap;
import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import agent.action.Action;
import agent.interaction.IndependantLearning;
import agent.interaction.MLKInteraction;
import environment.observation.Observation;
import environment.reward.Reward;
import environment.state.State;
import learning.Experience;
import madkit.simulation.environment.Environment2D;
import simulation.LearningData;
import util.Pair;

public abstract class EnvironmentStandard extends Environment2D implements MLKEnvironment {

	protected AgentsGroup agents;
	protected MLKInteraction interactionMethod;
	
	public EnvironmentStandard(int width, int height) {
        this(width, height, new IndependantLearning());
	}
	
	public EnvironmentStandard(int width, int height, MLKInteraction interactionMethod) {
		super(width, height);
		this.interactionMethod = interactionMethod;
    }
	
	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), "mlkenvironment");
		agents = new AgentsGroup();
	}

	public void receiveAgentInfo(MLKAgent agent) {
		agents.addAgent(agent);
	}
	protected abstract void setupState();

	/**
	 * Executes one environment step where all agents: 1.observe, 2.act, and 3.learn from the resulting experience.
	 *
	 * @return map of each agent to their experience for this step.
	 */
	public Map<MLKAgent, Experience> step(){
		Map<MLKAgent,Observation> AgentObservations = processSocialObservations();
		Map<MLKAgent, Pair<Action, Reward>> stepResult = EnvironmentStep(AgentObservations);
		Map<MLKAgent, Experience> experiences = feedExpToAgent(stepResult, AgentObservations);
		return experiences;
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
		Map<MLKAgent, Observation> interactionInformations = interactionMethod.getInteractionInformation(observations);
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
	public Map<MLKAgent, Pair<Action, Reward>> EnvironmentStep(Map<MLKAgent, Observation> observations){
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

	public Map<MLKAgent,Observation> getObservation(){
		return getState().getObservations();
	}
	
	public abstract Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions);
	
	protected void sendFeedbackExperience(Map<MLKAgent,Experience> experiences){
		for (Map.Entry<MLKAgent, Experience> entry : experiences.entrySet()) {
			MLKAgent agent = entry.getKey();
			Experience experience = entry.getValue();
			agent.feedbackExperience(experience);
		}
	}


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
	
	protected abstract State getState();
}
