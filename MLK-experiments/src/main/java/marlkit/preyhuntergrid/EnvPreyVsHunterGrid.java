package marlkit.preyhuntergrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2DInt;
import environment.EnvironmentStandard;
import environment.state.State;
import marlkit.preyhunter.agent.HunterAgent;
import marlkit.preyhunter.agent.PreyAgent;
import marlkit.preyhunter.environment.events.HunterHunterDistanceEvent;
import marlkit.preyhunter.environment.events.HunterPreyDistanceEvent;
import marlkit.preyhunter.environment.events.PreyCatchEvent;
import reward.ReactionEvent;
import reward.RewardModel;
import rewardmodelimplementation.MixedReward;
import util.Pair;

/**
 * Grid-based Prey vs Hunter environment.
 *
 * This version keeps agents on integer grid cells.
 */
public class EnvPreyVsHunterGrid extends EnvironmentStandard {

    protected StatePreyHunterGrid state;

    private static final int CAPTURE_DISTANCE = 1;

    public EnvPreyVsHunterGrid() {
        this(10, 10, new MixedReward());
    }

    public EnvPreyVsHunterGrid(int width, int height, RewardModel rewardModel) {
        super(width, height, rewardModel);
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        initState();
    }

    protected void initState() {
        this.state = new StatePreyHunterGrid(getWidth(), getHeight());
    }

    @Override
    protected void setupState() {
        state.reset();
    }

    @Override
    protected void setupAgents() {
        RandomGenerator rg = prng();

        for (MLKAgent agent : agents.getAgents()) {
            Pair<Integer, Integer> position = randomInitialPosition(agent, rg);
            state.addAgent(agent, position);
        }
    }

    @Override
    public void addAgent(MLKAgent agent) {
        agents.addAgent(agent);
    }

    @Override
    public void reset() {
        setupState();
        setupAgents();
    }

    @Override
    public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
        Map<MLKAgent, List<ReactionEvent>> results = initEmptyResults();

        moveAllAgents(actions);
        applyHunterHunterDistanceEvents(results);
        applyHunterPreyEvents(results);

        return results;
    }

    private Map<MLKAgent, List<ReactionEvent>> initEmptyResults() {
        Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();

        for (MLKAgent agent : agents.getAgents()) {
            results.put(agent, new ArrayList<>());
        }

        return results;
    }

    private void moveAllAgents(Map<MLKAgent, Action> actions) {
        for (MLKAgent agent : agents.getAgents()) {
            Action action = actions.get(agent);

            if (action == null) {
                continue;
            }

            if (!(action instanceof Move2DInt move)) {
                throw new IllegalArgumentException(
                        "EnvPreyVsHunterGrid expects Move2DInt actions, got: "
                                + action.getClass().getSimpleName()
                );
            }

            state.moveAgent(agent, move.getValue());
        }
    }

    private void applyHunterHunterDistanceEvents(Map<MLKAgent, List<ReactionEvent>> results) {
        List<HunterAgent> hunters = state.getHunterAgents();

        for (int i = 0; i < hunters.size(); i++) {
            HunterAgent hunter1 = hunters.get(i);

            for (int j = i + 1; j < hunters.size(); j++) {
                HunterAgent hunter2 = hunters.get(j);

                int distance = state.distance(hunter1, hunter2);

                results.get(hunter1).add(new HunterHunterDistanceEvent(distance));
                results.get(hunter2).add(new HunterHunterDistanceEvent(distance));
            }
        }
    }

    private void applyHunterPreyEvents(Map<MLKAgent, List<ReactionEvent>> results) {
        boolean caught = false;

        for (HunterAgent hunter : state.getHunterAgents()) {
            for (PreyAgent prey : state.getPreyAgents()) {
                int distance = state.distance(hunter, prey);

                results.get(hunter).add(new HunterPreyDistanceEvent(distance));

                if (state.isPreyCaught(hunter, prey, CAPTURE_DISTANCE)) {
                    caught = true;
                    results.get(hunter).add(new PreyCatchEvent());
                }
            }
        }

        if (caught) {
            reset();
        }
    }

    private Pair<Integer, Integer> randomInitialPosition(MLKAgent agent, RandomGenerator rg) {
        int x = rg.nextInt(getWidth());
        int y = rg.nextInt(getHeight() / 2);

        if (agent instanceof HunterAgent) {
            y += getHeight() / 2;
        }

        return new Pair<>(x, y);
    }

    public int distance2D(MLKAgent ag1, MLKAgent ag2) {
        return state.distance(ag1, ag2);
    }

    public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
        return state.getAgentsPositions();
    }

    public Map<MLKAgent, Pair<Integer, Integer>> getPreysPositions() {
        return state.getPreysPositions();
    }

    public Map<MLKAgent, Pair<Integer, Integer>> getHuntersPositions() {
        return state.getHuntersPositions();
    }

    @Override
    public State getState() {
        return state;
    }
}