package marlkit.preyhuntergrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.state.State2DGridInt;
import marlkit.preyhunter.agent.HunterAgent;
import marlkit.preyhunter.agent.PreyAgent;
import util.Pair;

/**
 * Grid-based state for the Prey-Hunter environment.
 *
 * This class keeps the old grid logic but wraps it in a cleaner,
 * experiment-specific state.
 */
public class StatePreyHunterGrid extends State2DGridInt {

    public static final int PREY_CELL_VALUE = 1;
    public static final int HUNTER_CELL_VALUE = 2;

    private final List<PreyAgent> preyAgents;
    private final List<HunterAgent> hunterAgents;

    public StatePreyHunterGrid(int width, int height) {
        super(width, height, Integer.MAX_VALUE, true, false);
        this.preyAgents = new ArrayList<>();
        this.hunterAgents = new ArrayList<>();
    }

    public StatePreyHunterGrid(int width, int height, int agentViewRange) {
        super(width, height, agentViewRange, true, false);
        this.preyAgents = new ArrayList<>();
        this.hunterAgents = new ArrayList<>();
    }

    @Override
    public void reset() {
        super.reset();
        agentsPosition.clear();
        preyAgents.clear();
        hunterAgents.clear();
    }

    @Override
    public void addAgent(MLKAgent agent, int x, int y) {
        addAgentwithVal(agent, x, y, getCellValue(agent));
        registerTypedAgent(agent);
    }

    public void addAgent(MLKAgent agent, Pair<Integer, Integer> position) {
        addAgent(agent, position.getFirst(), position.getSecond());
    }

    private void registerTypedAgent(MLKAgent agent) {
        if (agent instanceof PreyAgent prey && !preyAgents.contains(prey)) {
            preyAgents.add(prey);
        } else if (agent instanceof HunterAgent hunter && !hunterAgents.contains(hunter)) {
            hunterAgents.add(hunter);
        }
    }

    private int getCellValue(MLKAgent agent) {
        if (agent instanceof PreyAgent) {
            return PREY_CELL_VALUE;
        }
        if (agent instanceof HunterAgent) {
            return HUNTER_CELL_VALUE;
        }
        return 0;
    }

    public void moveAgent(MLKAgent agent, Pair<Integer, Integer> move) {
        moveAgentwithVal(agent, move, getCellValue(agent));
    }

    public int manhattanDistance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        return Math.abs(p1.getFirst() - p2.getFirst())
                + Math.abs(p1.getSecond() - p2.getSecond());
    }

    public int distance(MLKAgent a1, MLKAgent a2) {
        Pair<Integer, Integer> p1 = getAgentPosition(a1);
        Pair<Integer, Integer> p2 = getAgentPosition(a2);

        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Both agents must be registered in the state.");
        }

        return manhattanDistance(p1, p2);
    }

    public boolean isPreyCaught(HunterAgent hunter, PreyAgent prey, int captureDistance) {
        return distance(hunter, prey) <= captureDistance;
    }

    public Map<Pair<HunterAgent, PreyAgent>, Integer> getHunterPreyDistanceMap() {
        Map<Pair<HunterAgent, PreyAgent>, Integer> distances = new HashMap<>();

        for (HunterAgent hunter : hunterAgents) {
            for (PreyAgent prey : preyAgents) {
                distances.put(new Pair<>(hunter, prey), distance(hunter, prey));
            }
        }

        return distances;
    }

    public Map<MLKAgent, Pair<Integer, Integer>> getHuntersPositions() {
        Map<MLKAgent, Pair<Integer, Integer>> positions = new HashMap<>();

        for (HunterAgent hunter : hunterAgents) {
            Pair<Integer, Integer> position = getAgentPosition(hunter);
            if (position != null) {
                positions.put(hunter, position.clone());
            }
        }

        return positions;
    }

    public Map<MLKAgent, Pair<Integer, Integer>> getPreysPositions() {
        Map<MLKAgent, Pair<Integer, Integer>> positions = new HashMap<>();

        for (PreyAgent prey : preyAgents) {
            Pair<Integer, Integer> position = getAgentPosition(prey);
            if (position != null) {
                positions.put(prey, position.clone());
            }
        }

        return positions;
    }

    public List<PreyAgent> getPreyAgents() {
        return preyAgents;
    }

    public List<HunterAgent> getHunterAgents() {
        return hunterAgents;
    }
}