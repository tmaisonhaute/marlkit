package marlkit.teambattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agent.MLKAgent;
import agent.action.Action;
import environment.EnvironmentStandard;
import environment.state.State2DGridInt;
import marlkit.teambattle.ActionTeamBattle.Kind;
import marlkit.teambattle.events.AttackOpponentEvent;
import marlkit.teambattle.events.AttackPenaltyEvent;
import marlkit.teambattle.events.DeadPenaltyEvent;
import marlkit.teambattle.events.KillOpponentEvent;
import marlkit.teambattle.events.StepPenaltyEvent;
import rewardmodeling.ReactionEvent;
import rewardmodels.MixedReward;
import util.Pair;

/**
 * Team battle environment with movement, attack, hit points, and regeneration.
 *
 * <p>Agents belong to one of two teams and receive individual rewards.
 * The environment applies simultaneous movement resolution, directional attacks,
 * death handling, and per-turn health regeneration.</p>
 */
public class EnvTeamBattle extends EnvironmentStandard {

	public static final int DEFAULT_WIDTH = 10;
	public static final int DEFAULT_HEIGHT = 10;
	public static final int DEFAULT_OBSERVATION_RANGE = 3;
	public static final int DEFAULT_TEAM_SIZE = 10;

	private static final double MAX_HP = 5.0;
	private static final double ATTACK_DAMAGE = 2.0;
	private static final double HP_REGEN_PER_TURN = 0.2;

	private static final int EMPTY_CELL = 0;
	private static final int TEAM_A_CELL = 1;
	private static final int TEAM_B_CELL = 2;

	private State2DGridInt state;
	private final int observationRange;
	private final int teamSize;

	private final Map<MLKAgent, Integer> teams;
	private final Map<MLKAgent, Double> healthPoints;
	private final Set<MLKAgent> aliveAgents;
	private final Set<MLKAgent> deadPenaltyGiven;

	public EnvTeamBattle() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_OBSERVATION_RANGE, DEFAULT_TEAM_SIZE);
	}

	public EnvTeamBattle(int width, int height, int observationRange, int teamSize) {
		super(width, height, new MixedReward());
		this.observationRange = observationRange;
		this.teamSize = teamSize;
		this.teams = new HashMap<>();
		this.healthPoints = new HashMap<>();
		this.aliveAgents = new HashSet<>();
		this.deadPenaltyGiven = new HashSet<>();
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		state = new State2DGridInt(getWidth(), getHeight(), observationRange, true, false);
	}

	@Override
	public void addAgent(MLKAgent agent) {
		agents.addAgent(agent);
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
		int expectedAgentCount = teamSize * 2;
		if (agents.getAgents().size() != expectedAgentCount) {
			throw new IllegalStateException("TeamBattle requires exactly " + expectedAgentCount + " agents.");
		}

		teams.clear();
		healthPoints.clear();
		aliveAgents.clear();
		deadPenaltyGiven.clear();

		Set<Pair<Integer, Integer>> occupied = new HashSet<>();
		List<MLKAgent> allAgents = agents.getAgents();
		for (int i = 0; i < allAgents.size(); i++) {
			MLKAgent agent = allAgents.get(i);
			int team = i < teamSize ? TEAM_A_CELL : TEAM_B_CELL;
			Pair<Integer, Integer> spawn = sampleSpawn(team, occupied);
			occupied.add(spawn);
			state.addAgent(agent, spawn.getFirst(), spawn.getSecond());
			teams.put(agent, team);
			healthPoints.put(agent, MAX_HP);
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

		Map<MLKAgent, Pair<Integer, Integer>> moveCandidates = computeMoveCandidates(actions, previousPositions);
		Map<MLKAgent, Pair<Integer, Integer>> resolvedMoves = resolveMoveConflicts(previousPositions, moveCandidates);
		applyResolvedMoves(previousPositions, resolvedMoves);

		processAttacks(actions, eventsByAgent, previousPositions);
		processDeaths(eventsByAgent);
		regenerateHealth();
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
			if (!(action instanceof ActionTeamBattle teamAction)) {
				candidates.put(agent, current);
				continue;
			}

			if (teamAction.getKind() != Kind.MOVE) {
				candidates.put(agent, current);
				continue;
			}

			Pair<Integer, Integer> target = new Pair<>(
					current.getFirst() + teamAction.getDirection().getFirst(),
					current.getSecond() + teamAction.getDirection().getSecond());

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
			Map<MLKAgent, Pair<Integer, Integer>> moveCandidates) {

		Map<MLKAgent, Pair<Integer, Integer>> resolved = new HashMap<>();
		Map<Pair<Integer, Integer>, List<MLKAgent>> byTarget = new HashMap<>();

		for (MLKAgent agent : agents.getAgents()) {
			Pair<Integer, Integer> target = moveCandidates.get(agent);
			byTarget.computeIfAbsent(target, key -> new ArrayList<>()).add(agent);
		}

		for (Map.Entry<Pair<Integer, Integer>, List<MLKAgent>> entry : byTarget.entrySet()) {
			List<MLKAgent> contenders = entry.getValue();
			if (contenders.size() == 1) {
				MLKAgent only = contenders.get(0);
				resolved.put(only, moveCandidates.get(only));
				continue;
			}

			MLKAgent winner = contenders.get(prng().nextInt(contenders.size()));
			for (MLKAgent contender : contenders) {
				if (contender.equals(winner)) {
					resolved.put(contender, moveCandidates.get(contender));
				} else {
					resolved.put(contender, previousPositions.get(contender).clone());
				}
			}
		}

		return resolved;
	}

	private void applyResolvedMoves(
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions,
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

	private void processAttacks(Map<MLKAgent, Action> actions,
			Map<MLKAgent, List<ReactionEvent>> eventsByAgent,
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions) {

		Map<Pair<Integer, Integer>, MLKAgent> alivePositions = buildAliveAgentPositionIndex();

		for (MLKAgent agent : agents.getAgents()) {
			if (!aliveAgents.contains(agent)) {
				continue;
			}

			Action action = actions.get(agent);
			if (!(action instanceof ActionTeamBattle teamAction)) {
				continue;
			}

			if (teamAction.getKind() != Kind.ATTACK) {
				continue;
			}

			eventsByAgent.get(agent).add(new AttackPenaltyEvent());

			Pair<Integer, Integer> position = previousPositions.get(agent);
			Pair<Integer, Integer> target = new Pair<>(
					position.getFirst() + teamAction.getDirection().getFirst(),
					position.getSecond() + teamAction.getDirection().getSecond());

			MLKAgent targetAgent = alivePositions.get(target);
			if (targetAgent == null) {
				continue;
			}

			if (teams.get(targetAgent).equals(teams.get(agent))) {
				continue;
			}

			eventsByAgent.get(agent).add(new AttackOpponentEvent());
			damageTarget(targetAgent, ATTACK_DAMAGE);
			if (!aliveAgents.contains(targetAgent)) {
				eventsByAgent.get(agent).add(new KillOpponentEvent());
			}
		}
	}

	private void processDeaths(Map<MLKAgent, List<ReactionEvent>> eventsByAgent) {
		for (MLKAgent agent : agents.getAgents()) {
			if (!aliveAgents.contains(agent) && !deadPenaltyGiven.contains(agent)) {
				eventsByAgent.get(agent).add(new DeadPenaltyEvent());
				deadPenaltyGiven.add(agent);
			}
		}
	}

	private void regenerateHealth() {
		for (MLKAgent agent : aliveAgents) {
			double hp = healthPoints.get(agent);
			healthPoints.put(agent, Math.min(MAX_HP, hp + HP_REGEN_PER_TURN));
		}
	}

	private void damageTarget(MLKAgent targetAgent, double damage) {
		double hp = healthPoints.get(targetAgent) - damage;
		healthPoints.put(targetAgent, hp);
		if (hp <= 0.0) {
			aliveAgents.remove(targetAgent);
		}
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
			state.setValue(pos.getFirst(), pos.getSecond(), teams.get(agent));
		}
	}

	private Pair<Integer, Integer> sampleSpawn(int team, Set<Pair<Integer, Integer>> occupied) {
		while (true) {
			int x;
			if (team == TEAM_A_CELL) {
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

	public int getAliveCountTeamA() {
		return getAliveCountByTeam(TEAM_A_CELL);
	}

	public int getAliveCountTeamB() {
		return getAliveCountByTeam(TEAM_B_CELL);
	}

	public Map<MLKAgent, Double> getHealthPoints() {
		return new HashMap<>(healthPoints);
	}

	public Map<MLKAgent, Integer> getTeams() {
		return new HashMap<>(teams);
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getAliveAgentsPositions() {
		Map<MLKAgent, Pair<Integer, Integer>> positions = new HashMap<>();
		for (MLKAgent agent : aliveAgents) {
			positions.put(agent, state.getAgentPosition(agent).clone());
		}
		return positions;
	}

	private int getAliveCountByTeam(int team) {
		int count = 0;
		for (MLKAgent agent : aliveAgents) {
			if (teams.get(agent) == team) {
				count++;
			}
		}
		return count;
	}

	@Override
	public State2DGridInt getState() {
		return state;
	}
}
