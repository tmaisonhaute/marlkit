package marlkit.teamsurround;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.EnvironmentStandard;
import environment.state.State2DGridInt;
import marlkit.teamsurround.events.DeadPenaltyEvent;
import marlkit.teamsurround.events.StepPenaltyEvent;
import rewardmodeling.ReactionEvent;
import util.Pair;

/**
 * Environment for the TeamSurround experiment.
 *
 * <p>Agents move on a grid and may die when local enemy pressure dominates
 * ally support in the 4-neighborhood. The death rule can be deterministic or
 * stochastic depending on configuration.</p>
 */
public class EnvTeamSurround extends EnvironmentStandard {

	public static final int DEFAULT_WIDTH = 10;
	public static final int DEFAULT_HEIGHT = 10;
	public static final int DEFAULT_OBSERVATION_RANGE = 3;
	public static final int DEFAULT_TEAM_SIZE = 10;
	public static final boolean NEUMANN_NEIGHBORS = true;

	private static final int EMPTY_CELL = 0;
	private static final int TEAM_1_CELL = 1;
	private static final int TEAM_2_CELL = 2;

	private final int observationRange;
	private final int teamSize;
	private final boolean stochasticDeath;

	private State2DGridInt state;
	private final GroupAgentsTeam1 groupTeam1;
	private final GroupAgentsTeam2 groupTeam2;
	private final Set<MLKAgent> aliveAgents;

	public EnvTeamSurround() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_OBSERVATION_RANGE, DEFAULT_TEAM_SIZE, false);
	}

	public EnvTeamSurround(boolean stochasticDeath) {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_OBSERVATION_RANGE, DEFAULT_TEAM_SIZE, stochasticDeath);
	}

	public EnvTeamSurround(int width, int height, int observationRange, int teamSize, boolean stochasticDeath) {
		super(width, height, new RewardModelTeam());
		this.observationRange = observationRange;
		this.teamSize = teamSize;
		this.stochasticDeath = stochasticDeath;
		this.groupTeam1 = new GroupAgentsTeam1();
		this.groupTeam2 = new GroupAgentsTeam2();
		this.aliveAgents = new HashSet<>();
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		state = new State2DGridInt(getWidth(), getHeight(), observationRange, NEUMANN_NEIGHBORS, false);
	}

	@Override
	public void addAgent(MLKAgent agent) {
		if (agent instanceof AgentTeam1) {
			groupTeam1.addAgent(agent);
			agents.addAgent(agent);
			return;
		}
		if (agent instanceof AgentTeam2) {
			groupTeam2.addAgent(agent);
			agents.addAgent(agent);
			return;
		}
		throw new IllegalArgumentException("Only AgentTeam1 and AgentTeam2 are allowed in TeamSurround.");
	}

	@Override
	public void setupState() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				state.setValue(x, y, EMPTY_CELL);
			}
		}
	}

	@Override
	public void setupAgents() {
		if (groupTeam1.getAgents().size() != teamSize || groupTeam2.getAgents().size() != teamSize) {
			throw new IllegalStateException("TeamSurround requires exactly " + teamSize + " agents per team.");
		}

		aliveAgents.clear();
		Set<Pair<Integer, Integer>> occupied = new HashSet<>();

		for (MLKAgent agent : groupTeam1.getAgents()) {
			Pair<Integer, Integer> spawn = sampleSpawn(TEAM_1_CELL, occupied);
			occupied.add(spawn);
			state.addAgent(agent, spawn.getFirst(), spawn.getSecond());
			aliveAgents.add(agent);
		}

		for (MLKAgent agent : groupTeam2.getAgents()) {
			Pair<Integer, Integer> spawn = sampleSpawn(TEAM_2_CELL, occupied);
			occupied.add(spawn);
			state.addAgent(agent, spawn.getFirst(), spawn.getSecond());
			aliveAgents.add(agent);
		}

		rebuildGridWithAliveAgents();
	}

	@Override
	public void reset() {
		state.reset();
		setupState();
		setupAgents();
	}

	@Override
	public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, List<ReactionEvent>> eventsByAgent = initializeEvents();
		Map<MLKAgent, Pair<Integer, Integer>> previousPositions = state.getAgentsPositions();

		Map<MLKAgent, Pair<Integer, Integer>> candidates = computeMoveCandidates(actions, previousPositions);
		Map<MLKAgent, Pair<Integer, Integer>> resolvedMoves = resolveMoveConflicts(previousPositions, candidates);
		applyResolvedMoves(previousPositions, resolvedMoves);

		List<MLKAgent> toDie = computeAgentsToDie();
		for (MLKAgent agent : toDie) {
			aliveAgents.remove(agent);
			eventsByAgent.get(agent).add(new DeadPenaltyEvent());
		}

		rebuildGridWithAliveAgents();
		return eventsByAgent;
	}

	private Map<MLKAgent, List<ReactionEvent>> initializeEvents() {
		Map<MLKAgent, List<ReactionEvent>> eventsByAgent = new HashMap<>();
		for (MLKAgent agent : agents.getAgents()) {
			List<ReactionEvent> events = new ArrayList<>();
			eventsByAgent.put(agent, events);
			if (aliveAgents.contains(agent)) {
				events.add(new StepPenaltyEvent());
			}
		}
		return eventsByAgent;
	}

	private Map<MLKAgent, Pair<Integer, Integer>> computeMoveCandidates(
			Map<MLKAgent, Action> actions,
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions) {

		Map<MLKAgent, Pair<Integer, Integer>> candidates = new HashMap<>();
		for (MLKAgent agent : agents.getAgents()) {
			Pair<Integer, Integer> current = previousPositions.get(agent).clone();
			if (!aliveAgents.contains(agent)) {
				candidates.put(agent, current);
				continue;
			}

			Action action = actions.get(agent);
			if (!(action instanceof Action2DMove moveAction)) {
				candidates.put(agent, current);
				continue;
			}

			Pair<Integer, Integer> target = new Pair<>(
					current.getFirst() + moveAction.getValue().getFirst(),
					current.getSecond() + moveAction.getValue().getSecond());

			if (!isInside(target)) {
				candidates.put(agent, current);
				continue;
			}

			if (isOccupiedByAliveAgent(target, previousPositions)) {
				candidates.put(agent, current);
				continue;
			}

			candidates.put(agent, target);
		}
		return candidates;
	}

	private Map<MLKAgent, Pair<Integer, Integer>> resolveMoveConflicts(
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions,
			Map<MLKAgent, Pair<Integer, Integer>> candidates) {

		Map<MLKAgent, Pair<Integer, Integer>> resolved = new HashMap<>();
		Map<Pair<Integer, Integer>, List<MLKAgent>> byTarget = new HashMap<>();

		for (MLKAgent agent : agents.getAgents()) {
			Pair<Integer, Integer> target = candidates.get(agent);
			byTarget.computeIfAbsent(target, key -> new ArrayList<>()).add(agent);
		}

		for (Map.Entry<Pair<Integer, Integer>, List<MLKAgent>> entry : byTarget.entrySet()) {
			List<MLKAgent> contenders = entry.getValue();
			if (contenders.size() == 1) {
				MLKAgent only = contenders.get(0);
				resolved.put(only, candidates.get(only));
				continue;
			}

			MLKAgent winner = contenders.get(prng().nextInt(contenders.size()));
			for (MLKAgent contender : contenders) {
				if (contender.equals(winner)) {
					resolved.put(contender, candidates.get(contender));
				} else {
					resolved.put(contender, previousPositions.get(contender).clone());
				}
			}
		}

		return resolved;
	}

	private void applyResolvedMoves(Map<MLKAgent, Pair<Integer, Integer>> previousPositions,
			Map<MLKAgent, Pair<Integer, Integer>> resolvedMoves) {

		for (MLKAgent agent : agents.getAgents()) {
			Pair<Integer, Integer> previous = previousPositions.get(agent);
			Pair<Integer, Integer> resolved = resolvedMoves.get(agent);
			Pair<Integer, Integer> delta = new Pair<>(
					resolved.getFirst() - previous.getFirst(),
					resolved.getSecond() - previous.getSecond());
			state.moveAgent(agent, delta);
		}
	}

	private List<MLKAgent> computeAgentsToDie() {
		List<MLKAgent> toDie = new ArrayList<>();
		Map<Pair<Integer, Integer>, MLKAgent> index = buildAliveAgentPositionIndex();
		for (MLKAgent agent : aliveAgents) {
			Pair<Integer, Integer> pos = state.getAgentPosition(agent).clone();
			int team = getTeamCode(agent);
			int allies = 1;
			int enemies = 0;
			for (Pair<Integer, Integer> neighbor : directNeighbors(pos)) {
				MLKAgent neighborAgent = index.get(neighbor);
				if (neighborAgent == null) {
					continue;
				}
				if (getTeamCode(neighborAgent) == team) {
					allies++;
				} else {
					enemies++;
				}
			}

			if (stochasticDeath) {
				computeStochasticDeath(enemies, allies, toDie, agent);
			} else if (enemies > allies) {
				toDie.add(agent);
			}
		}
		return toDie;
	}

    private void computeStochasticDeath(int enemies, int allies,  List<MLKAgent> toDie, MLKAgent agent) {
        double deathProbability = (double) enemies / (allies + enemies);
        deathProbability = Math.pow(deathProbability, 2);
        if (prng().nextDouble() < deathProbability) {
            toDie.add(agent);
        }
    }

	private List<Pair<Integer, Integer>> directNeighbors(Pair<Integer, Integer> pos) {
		List<Pair<Integer, Integer>> neighbors = new ArrayList<>(4);
		neighbors.add(new Pair<>(pos.getFirst() - 1, pos.getSecond()));
		neighbors.add(new Pair<>(pos.getFirst() + 1, pos.getSecond()));
		neighbors.add(new Pair<>(pos.getFirst(), pos.getSecond() - 1));
		neighbors.add(new Pair<>(pos.getFirst(), pos.getSecond() + 1));
		return neighbors;
	}

	private Map<Pair<Integer, Integer>, MLKAgent> buildAliveAgentPositionIndex() {
		Map<Pair<Integer, Integer>, MLKAgent> aliveIndex = new HashMap<>();
		for (MLKAgent agent : aliveAgents) {
			Pair<Integer, Integer> position = state.getAgentPosition(agent).clone();
			aliveIndex.put(position, agent);
		}
		return aliveIndex;
	}

	private void rebuildGridWithAliveAgents() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				state.setValue(x, y, EMPTY_CELL);
			}
		}

		for (MLKAgent agent : aliveAgents) {
			Pair<Integer, Integer> pos = state.getAgentPosition(agent).clone();
			state.setValue(pos.getFirst(), pos.getSecond(), getTeamCode(agent));
		}
	}

	private Pair<Integer, Integer> sampleSpawn(int team, Set<Pair<Integer, Integer>> occupied) {
		while (true) {
			int x;
			if (team == TEAM_1_CELL) {
				x = prng().nextInt(Math.max(1, getWidth() / 2));
			} else {
				x = getWidth() / 2 + prng().nextInt(Math.max(1, getWidth() - (getWidth() / 2)));
			}
			int y = prng().nextInt(getHeight());
			Pair<Integer, Integer> position = new Pair<>(x, y);
			if (!occupied.contains(position)) {
				return position;
			}
		}
	}

	private boolean isInside(Pair<Integer, Integer> position) {
		return position.getFirst() >= 0
				&& position.getFirst() < getWidth()
				&& position.getSecond() >= 0
				&& position.getSecond() < getHeight();
	}

	private boolean isOccupiedByAliveAgent(Pair<Integer, Integer> position,
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions) {
		for (MLKAgent agent : aliveAgents) {
			if (previousPositions.get(agent).equals(position)) {
				return true;
			}
		}
		return false;
	}

	private int getTeamCode(MLKAgent agent) {
		if (agent instanceof AgentTeam1) {
			return TEAM_1_CELL;
		}
		if (agent instanceof AgentTeam2) {
			return TEAM_2_CELL;
		}
		throw new IllegalStateException("Unknown team agent type.");
	}

	public boolean isStochasticDeath() {
		return stochasticDeath;
	}

	public int getAliveCountTeam1() {
		int count = 0;
		for (MLKAgent agent : aliveAgents) {
			if (agent instanceof AgentTeam1) {
				count++;
			}
		}
		return count;
	}

	public int getAliveCountTeam2() {
		int count = 0;
		for (MLKAgent agent : aliveAgents) {
			if (agent instanceof AgentTeam2) {
				count++;
			}
		}
		return count;
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getAliveAgentsPositions() {
		Map<MLKAgent, Pair<Integer, Integer>> positions = new HashMap<>();
		for (MLKAgent agent : aliveAgents) {
			positions.put(agent, state.getAgentPosition(agent).clone());
		}
		return positions;
	}

	public int getTeamAt(MLKAgent agent) {
		return getTeamCode(agent);
	}

	@Override
	public State2DGridInt getState() {
		return state;
	}
}
