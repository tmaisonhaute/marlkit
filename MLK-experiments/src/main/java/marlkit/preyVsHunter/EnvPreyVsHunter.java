package marlkit.preyVsHunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2D;
import environment.EnvironmentStandard;
import environment.state.State;
import environment.state.State2DGridInt;
import marlkit.preyVsHunter.events.HunterDistancePenalty;
import marlkit.preyVsHunter.events.PreyCatchEvent;
import reward.ReactionEvent;
import reward.Reward;
import reward.RewardStandard;
import rewardmodelimplementation.FullyCooperativeReward;
import util.Pair;


public class EnvPreyVsHunter extends EnvironmentStandard {

    protected State2DGridInt state;

    private final List<PreyAgent> preyAgents = new ArrayList<>();
    private final List<HunterAgent> hunterAgents= new ArrayList<>();

//    private static final double REWARDPREYCATCH = 100;
    private static final double REWARDDISTANCEPENALTYPxH = 2;
    private static final double REWARDDISTANCEPENALTYHxH = 0.1;
    private static final double REWARDTOONEAR = 0.5;

    public EnvPreyVsHunter() {
        super(10, 10, new FullyCooperativeReward());
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        state = new State2DGridInt(getWidth(), getHeight());
        setupState();
    }

    @Override
    public void setupState() {
    }
    
    @Override
    public void setupAgents() {
    	for(MLKAgent ag : agents.getAgents()) {
    		RandomGenerator rg = prng();
            int i = rg.nextInt(getWidth());
            int j = rg.nextInt(getHeight()/2);

            if (ag instanceof PreyAgent) {
                preyAgents.add((PreyAgent) ag);
            }
            if (ag instanceof HunterAgent) {
                hunterAgents.add((HunterAgent) ag);
                j += getHeight()/2;
            }

            state.addAgentwithVal(ag, i, j,getclassId(ag));
    	}
    }

    @Override
    public void addAgent(MLKAgent agent) {
        agents.addAgent(agent);
    }

    @Override
    public void reset() {
        state.reset();
        RandomGenerator rg = prng();
        for (MLKAgent ag : agents.getAgents()) {
            int i = rg.nextInt(getWidth());
            int j = rg.nextInt(getHeight() / 2);
            if (ag instanceof HunterAgent) {
                j += getHeight() / 2;
            }

            state.addAgentwithVal(ag, i, j, getclassId(ag));
        }
    }

    @Override
    public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
        Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();
        moveAllAgents(actions);
        double hunterDistancePenaltyReward = AllHxHDistancePenalityRewardValue();
		for (MLKAgent ag : hunterAgents) {
			HunterDistancePenalty distanceEvent = new HunterDistancePenalty(hunterDistancePenaltyReward);
			results.put(ag, distanceEvent.toList());
        }
		computeEventCatch(results);
        return results;
    }

    /**
     * All agents in the Hashmap move on the grid based on their action
     *
     * @param actions a map of agents to their respective actions
     */
    private void moveAllAgents(Map<MLKAgent, Action> actions) {
        for (MLKAgent ag : agents.getAgents()) {
            Move2D action = (Move2D) actions.get(ag);
            state.moveAgentwithVal(ag, action.getValue(), getclassId(ag));
            
        }
    }
    
    private void computeEventCatch(Map<MLKAgent, List<ReactionEvent>> results){
    	boolean caught = false;
    	
    	for (MLKAgent Hag : hunterAgents) {
            for (MLKAgent Pag : preyAgents) {
                int distance = distance2D(Hag,Pag);
                HunterDistancePenalty penalty = new HunterDistancePenalty(- distance * REWARDDISTANCEPENALTYPxH);
                results.get(Hag).add(penalty);
                if (distance == 1){
                	caught = true;
                	results.get(Hag).add(new PreyCatchEvent());
                }
            }
        }
		if (caught) {
        	reset();
		}
    }

//    /**
//     * Calculate the total shared reward for all hunter agents.
//     *
//     * This reward is based on two main components:
//     * - A penalty based on the distances between hunters ({@code AllHxHDistancePenalityRewardValue})
//     * - A reward or penalty based on the distances between hunters and prey ({@code AllPxHRewardValueFromDistance})
//     *
//     * @return The total calculated reward as a double
//     */
//    private double CalculateHunterReward(){
//        double reward = 0;
//        reward += AllHxHDistancePenalityRewardValue();
//        reward += AllPxHRewardValueFromDistance();
//        return reward;
//    }

    /**
     * Computes the total penalty based on distances between all pairs of hunter agents.
     *
     * Applies a distance-based penalty using {@code HtoHDistancePenality} for each unique pair.
     *
     * @return total hunter-to-hunter distance penalty
     */
    private double AllHxHDistancePenalityRewardValue() {
        int size = hunterAgents.size();
        if (size < 2) {
            return 0;
        }
        double penalityreward = 0 ;
        for (int i = 0; i < size; i++) {
            for (int y = i + 1; y < size; y++) {
                MLKAgent hunter1 = hunterAgents.get(i);
                MLKAgent hunter2 = hunterAgents.get(y);
                int distance = distance2D(hunter1,hunter2);
                penalityreward += HtoHDistancePenality(distance);
            }
        }
        return penalityreward;
    }

    /**
     * Computes the penalty for a given distance between two hunter agents.
     *
     * @param distance the 2D distance between two hunters
     * @return the calculated penalty as a negative value
     */
    private double HtoHDistancePenality(int distance) {
        if (distance <= 1) {
            return - REWARDTOONEAR;
        }else {
            return - (distance - 1) * REWARDDISTANCEPENALTYHxH;
        }
    }

//    /**
//     * Calculates the total reward based on distances between hunters and prey.
//     *
//     * if Prey is captured, {@code reset} all and return a specific reward {@code REWARDPREYCATCH}
//     * , otherwise applies a distance penalty.
//     *
//     * @return the total hunter-prey reward value
//     */
//    private Event AllPxHRewardValueFromDistance(){
//        double reward = 0;
//        for (MLKAgent Hag : hunterAgents) {
//            for (MLKAgent Pag : preyAgents) {
//                int distance = distance2D(Hag,Pag);
//                if (distance == 1){
//                    reset();
//                    return REWARDPREYCATCH;
//                }
//                reward +=  - distance * REWARDDISTANCEPENALTYPxH;
//            }
//        }
//        return reward;
//    }

    /**
     * Computes the distance between two grid positions.
     *
     * @param p1 The first position as a pair of (x, y) coordinates.
     * @param p2 The second position as a pair of (x, y) coordinates.
     * @return The distance between the two positions.
     */
    public int distance2D(Pair<Integer, Integer> p1 , Pair<Integer, Integer>p2){
        return Math.abs((p1.getFirst() - p2.getFirst())) + Math.abs(p1.getSecond() - p2.getSecond());
    }
    /**
     * Computes the distance between two agents based on their positions in the environment.
     *
     * @param ag1 The first agent.
     * @param ag2 The second agent.
     * @return The distance between the two agents' positions.
     */
    public int distance2D(MLKAgent ag1 , MLKAgent ag2){
        Pair<Integer,Integer> pos1 =state.getAgentPosition(ag1).clone();
        Pair<Integer,Integer> pos2 =state.getAgentPosition(ag2).clone();
        return distance2D(pos1,pos2);
    }

    /**
     * Assigns the same collective reward to a list of agents based on their actions.
     *
     * Each agent receives the given reward paired with their corresponding action.
     *
     * @param agents  list of agents to reward
     * @param reward  reward value to assign
     * @param actions map of agents to their actions
     * @param results map to store resulting (action, reward) pairs per agent
     */
    private void addCollectifReward(List<? extends MLKAgent> agents,double reward, Map<MLKAgent, Action> actions, Map<MLKAgent, Pair<Action, Reward>> results) {
        for (MLKAgent ag : agents) {
            Reward R = new RewardStandard(reward);
            Move2D action = (Move2D) actions.get(ag);
            results.put(ag, new Pair<>(action, R));
        }
    }

    /**
     * Returns a numeric class identifier for the given agent.
     * (draft method, need to be changed for more effective)
     *
     * @param agent The agent to classify.
     * @return {@code 1} if the agent is a {@link PreyAgent}, {@code 2} otherwise (e.g., for {@link HunterAgent}).
     */
    public int getclassId(MLKAgent agent) { //fonction brouillon
        if (agent instanceof PreyAgent){return 1;}
        else {return 2;}
    }

    /**
     * Computes the 2D distances between all pairs of hunters and preys.
     *
     * @return A map where each key is a (HunterAgent, PreyAgent) pair,
     *         and the value is the distance between them.
     */
    private Map<Pair<HunterAgent,PreyAgent> , Integer> getHunterPreydistanceMap(){
        Map<Pair<HunterAgent,PreyAgent> , Integer> res = new HashMap<>();
        for (HunterAgent Hag : hunterAgents) {
            for (PreyAgent Pag : preyAgents) {
                res.put(new Pair<>(Hag,Pag),distance2D(Hag,Pag));
            }
        }
        return res;
    }

    /**
     * Retrieves the current positions of all agents (prey and hunter) in the environment.
     *
     * @return A map associating each {@link MLKAgent} with its current (x, y) position on the grid.
     */
    public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
        return state.getAgentsPositions();
    }

    /**
     * Retrieves the current positions of all prey agents in the environment.
     *
     * @return A map associating each {@link PreyAgent} with its (x, y) position on the grid.
     */
    public Map<MLKAgent, Pair<Integer, Integer>> getPreysPositions(){
        Map<MLKAgent, Pair<Integer, Integer>> results = new HashMap<>();
        for (MLKAgent ag : preyAgents) {
            results.put(ag, state.getAgentPosition(ag).clone());
        }
        return results;
    }

    /**
     * Retrieves the current positions of all hunter agents in the environment.
     *
     * @return A map associating each {@link HunterAgent} with its (x, y) position on the grid.
     */
    public Map<MLKAgent, Pair<Integer, Integer>> getHuntersPositions(){
        Map<MLKAgent, Pair<Integer, Integer>> results = new HashMap<>();
        for (MLKAgent ag : hunterAgents) {
            results.put(ag,state.getAgentPosition(ag).clone());
        }
        return results;
    }

    /**
     * Returns the current state object representing the 2D grid of the environment.
     *
     * @return The {@link State2DGridInt} representing agent positions and the environment state.
     */
    public State getState() {
        return state;
    }
}