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

	public Map<MLKAgent, Experience> step(){
		Map<MLKAgent, Observation> observations = getObservation();
		Map<MLKAgent, Observation> interactionInformations = interactionMethod.getInteractionInformation(observations);
		Map<MLKAgent, Observation> mergedObservations = mergeObservations(observations, interactionInformations);
		
		Map<MLKAgent, Action> actions = agents.allAgentsTakeAction(mergedObservations);
		Map<MLKAgent, Pair<Action, Reward>> result = dynamics(actions);
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
