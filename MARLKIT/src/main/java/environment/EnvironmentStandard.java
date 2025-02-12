package environment;

import java.util.HashMap;
import java.util.Map;

import agent.AgentsGroup;
import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;
import environment.state.State;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuEnvironment;
import util.Pair;
import util.Triple;

public abstract class EnvironmentStandard extends SimuEnvironment implements MLKEnvironment {

	protected AgentsGroup agents;
	protected State state;
	
	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), "mlkenvironment");
		agents = new AgentsGroup();
	}
	public void receiveAgentsInfo() {
		ObjectMessage<MLKAgent> messageAgent = getMailbox().next();
		while (messageAgent != null){
			MLKAgent agent = messageAgent.getContent();
			agents.addAgent(agent);
		}
		setup();
	}
	protected abstract void setup();
	
	/**
     * Resets the environment to its initial state.
     * 
     * @return a map of agents to their initial observations
     */
	public abstract void reset();
	
//	@Override
//	protected void onLive() {
//		super.onLive();
//		step();
//	}
	

	public Map<MLKAgent, Triple<Observation, Action, Reward>> step(){
		Map<MLKAgent,Observation> observations = getObservation();
		Map<MLKAgent, Action> actions = new HashMap<>();
		actions = agents.allAgentsTakeAction(observations);
		Map<MLKAgent, Pair<Action, Reward>> result = dynamics(actions);
		printState();
		return combine_obs_act_reward(result, observations);
		
	}
	public Map<MLKAgent,Observation> getObservation(){
		return state.getObservations();
	}
	
	public abstract Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions);
	
	protected Map<MLKAgent, Triple<Observation, Action, Reward>> combine_obs_act_reward(Map<MLKAgent, Pair<Action, Reward>> actionRewardMap,
			Map<MLKAgent, Observation> observationMap ){
		Map<MLKAgent, Triple<Observation, Action, Reward>> combinedMap = new HashMap<>();
		
		for (Map.Entry<MLKAgent, Observation> entry : observationMap.entrySet()) {
			MLKAgent agent = entry.getKey();
			Observation observation = entry.getValue();
			Pair<Action, Reward> actionRewardPair = actionRewardMap.get(agent);
			
			if (actionRewardPair != null) {
				Triple<Observation, Action, Reward> triple = new Triple<>(
						observation,
						actionRewardPair.getFirst(),
						actionRewardPair.getSecond()
						);
				combinedMap.put(agent, triple);
			}
		}
		return combinedMap;
	}

	protected void printState() {
		state.print();
	}
	
}
