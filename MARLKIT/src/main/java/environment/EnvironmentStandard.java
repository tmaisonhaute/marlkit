package environment;

import java.util.HashMap;
import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;
import environment.state.State;
import learning.Experience;
import madkit.simulation.environment.Environment2D;
import util.Pair;

public abstract class EnvironmentStandard extends Environment2D implements MLKEnvironment {

	protected AgentsGroup agents;
	
	public EnvironmentStandard(int width, int height) {
        super(width, height);
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
	@Override
	public abstract void setupAgent(MLKAgent agent);
	
	/**
     * Resets the environment to its initial state.
     * 
     * @return a map of agents to their initial observations
     */
	public abstract void reset();
	

	public Map<MLKAgent, Experience> step(){
		Map<MLKAgent,Observation> observations = getObservation();
		Map<MLKAgent, Action> actions = new HashMap<>();
		actions = agents.allAgentsTakeAction(observations);
		Map<MLKAgent, Pair<Action, Reward>> result = dynamics(actions);
		Map<MLKAgent, Experience> experiences = combine_obs_act_reward(result, observations);
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
	

	protected Map<MLKAgent, Experience> combine_obs_act_reward(Map<MLKAgent, Pair<Action, Reward>> actionRewardMap,
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


	protected void printState() {
		getState().print();
	}
	
	protected abstract State getState();

	
}
