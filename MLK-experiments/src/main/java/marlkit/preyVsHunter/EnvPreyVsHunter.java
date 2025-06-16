package marlkit.preyVsHunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import agent.interaction.FictitiousPlay;
import environment.EnvironmentStandard;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import environment.state.State;
import environment.state.State2DGridInt;
import util.Pair;


public class EnvPreyVsHunter extends EnvironmentStandard {

    protected State2DGridInt state;

    private final List<PreyAgent> preyAgents = new ArrayList<>();
    private final List<HunterAgent> hunterAgents= new ArrayList<>();

    private static final double REWARDPREYCATCH = 100;
    private static final double REWARDDISTANCEPENALTYPxH = 2;
    private static final double REWARDDISTANCEPENALTYHxH = 0.1;
    private static final double REWARDTOONEAR = 0.5;

    public EnvPreyVsHunter() {
        super(10, 10, new FictitiousPlay());
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
    public void setupAgent(MLKAgent agent) {
        agents.addAgent(agent);
        RandomGenerator rg = prng();
        int i = rg.nextInt(getWidth());
        int j = rg.nextInt(getHeight()/2);

        if (agent instanceof PreyAgent) {
            preyAgents.add((PreyAgent) agent);
        }
        if (agent instanceof HunterAgent) {
            hunterAgents.add((HunterAgent) agent);
            j += getHeight()/2;
        }

        state.addAgentwithVal(agent, i, j,getclassId(agent));
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
    public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
        Map<MLKAgent, Pair<Action, Reward>> results = new HashMap<>();
        moveAllAgents(actions);
        double hunterReward = CalculateHunterReward();
        double preyReward = 1;
        addCollectifReward(hunterAgents,hunterReward,actions,results);
        addCollectifReward(preyAgents,preyReward,actions,results);
        return results;
    }

    /**
     * All agents in the Hashmap move on the grid based on their action
     *
     * @param actions a map of agents to their respective actions
     */
    private void moveAllAgents(Map<MLKAgent, Action> actions) {
        for (MLKAgent ag : agents.getAgents()) {
            Action2DMove action = (Action2DMove) actions.get(ag);
            state.moveAgentwithVal(ag, action.getValue(), getclassId(ag));
        }
    }

    /**
     * Calculate the total shared reward for all hunter agents.
     *
     * This reward is based on two main components:
     * - A penalty based on the distances between hunters ({@code AllHxHDistancePenalityRewardValue})
     * - A reward or penalty based on the distances between hunters and prey ({@code AllPxHRewardValueFromDistance})
     *
     * @return The total calculated reward as a double
     */
    private double CalculateHunterReward(){
        double reward = 0;
        reward += AllHxHDistancePenalityRewardValue();
        reward += AllPxHRewardValueFromDistance();
        return reward;
    }

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

    /**
     * Calculates the total reward based on distances between hunters and prey.
     *
     * if Prey is captured, {@code reset} all and return a specific reward {@code REWARDPREYCATCH}
     * , otherwise applies a distance penalty.
     *
     * @return the total hunter-prey reward value
     */
    private double AllPxHRewardValueFromDistance(){
        double reward = 0;
        for (MLKAgent Hag : hunterAgents) {
            for (MLKAgent Pag : preyAgents) {
                int distance = distance2D(Hag,Pag);
                if (distance == 1){
                    reset();
                    return REWARDPREYCATCH;
                }
                reward +=  - distance * REWARDDISTANCEPENALTYPxH;
            }
        }
        return reward;
    }

    public int distance2D(Pair<Integer, Integer> p1 , Pair<Integer, Integer>p2){
        int distance = Math.abs((p1.getFirst() - p2.getFirst())) + Math.abs(p1.getSecond() - p2.getSecond()) ;
        if (distance >= 5){
            getLogger().log(Level.FINEST,"distance2D: "+distance+ "ag1 :"+p1+", ag2 :"+p2);
        }
        return Math.abs((p1.getFirst() - p2.getFirst())) + Math.abs(p1.getSecond() - p2.getSecond()) ;
    }
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
            Action2DMove action = (Action2DMove) actions.get(ag);
            results.put(ag, new Pair<>(action, R));
        }
    }

    public int getclassId(MLKAgent agent) { //fonction brouillon
        if (agent instanceof PreyAgent){return 1;}
        else {return 2;}
    }

    private Map<Pair<HunterAgent,PreyAgent> , Integer> getHunterPreydistanceMap(){
        Map<Pair<HunterAgent,PreyAgent> , Integer> res = new HashMap<>();
        for (HunterAgent Hag : hunterAgents) {
            for (PreyAgent Pag : preyAgents) {
                res.put(new Pair<>(Hag,Pag),distance2D(Hag,Pag));
            }
        }
        return res;
    }

    public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
        return state.getAgentsPositions();
    }

    public Map<MLKAgent, Pair<Integer, Integer>> getPreysPositions(){
        Map<MLKAgent, Pair<Integer, Integer>> results = new HashMap<>();
        for (MLKAgent ag : preyAgents) {
            results.put(ag, state.getAgentPosition(ag).clone());
        }
        return results;
    }

    public Map<MLKAgent, Pair<Integer, Integer>> getHuntersPositions(){
        Map<MLKAgent, Pair<Integer, Integer>> results = new HashMap<>();
        for (MLKAgent ag : hunterAgents) {
            results.put(ag,state.getAgentPosition(ag).clone());
        }
        return results;
    }

    protected State getState() {
        return state;
    }
}