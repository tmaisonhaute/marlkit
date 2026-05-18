package marlkit.crossescape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2D;
import environment.EnvironmentStandard;
import environment.state.State2DGridInt;
import marlkit.crossescape.events.EscapedEvent;
import marlkit.crossescape.events.NotEscapedYetEvent;
import marlkit.crossescape.events.NothingEvent;
import reward.ReactionEvent;
import rewardmodelimplementation.MixedReward;
import util.Pair;

/**
 * Environment for the CrossEscape experiment.
 *
 * <p>Four agents start on the extremities of a cross-shaped corridor and must
 * reach the opposite extremity. The dynamics enforce collision constraints and
 * resolve simultaneous target conflicts with a seeded random tie-break.</p>
 */
public class EnvCrossEscape extends EnvironmentStandard {

	public static final int GOAL_REACHED_MARKER = 1;

	private static final int AGENT_COUNT = 4;
	private static final int WALL_VALUE = -1;
	private static final int EMPTY_VALUE = 0;

	private State2DGridInt state;
	private final Map<MLKAgent, Pair<Integer, Integer>> startPositions;
	private final Map<MLKAgent, Pair<Integer, Integer>> goalPositions;
	private final Set<MLKAgent> escapedAgents;

	public EnvCrossEscape() {
		this(7, 7);
	}

	public EnvCrossEscape(int width, int height) {
		super(width, height, new MixedReward());
		if (width % 2 == 0 || height % 2 == 0) {
			throw new IllegalArgumentException("Cross map requires odd width and height.");
		}
		this.startPositions = new HashMap<>();
		this.goalPositions = new HashMap<>();
		this.escapedAgents = new HashSet<>();
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		state = new State2DGridInt(getWidth(), getHeight(), true, true);
	}

	@Override
	public void addAgent(MLKAgent agent) {
		agents.addAgent(agent);
	}

	@Override
	public void setupState() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				state.setValue(x, y, isCrossCell(x, y) ? EMPTY_VALUE : WALL_VALUE);
			}
		}
	}

	@Override
	public void setupAgents() {
		if (agents.getAgents().size() != AGENT_COUNT) {
			throw new IllegalStateException("CrossEscape requires exactly 4 agents.");
		}

		startPositions.clear();
		goalPositions.clear();
		escapedAgents.clear();

		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		List<Pair<Integer, Integer>> starts = List.of(
				new Pair<>(0, centerY),
				new Pair<>(getWidth() - 1, centerY),
				new Pair<>(centerX, 0),
				new Pair<>(centerX, getHeight() - 1));
		List<Pair<Integer, Integer>> goals = List.of(
				new Pair<>(getWidth() - 1, centerY),
				new Pair<>(0, centerY),
				new Pair<>(centerX, getHeight() - 1),
				new Pair<>(centerX, 0));

		for (int i = 0; i < AGENT_COUNT; i++) {
			MLKAgent agent = agents.getAgents().get(i);
			Pair<Integer, Integer> start = starts.get(i);
			Pair<Integer, Integer> goal = goals.get(i);
			state.addAgent(agent, start.getFirst(), start.getSecond());
			startPositions.put(agent, start);
			goalPositions.put(agent, goal);
		}
	}

	@Override
	public void reset() {
		state.reset();
		setupState();
		setupAgents();
	}

	@Override
	public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
		Map<MLKAgent, List<ReactionEvent>> reactionEvents = new HashMap<>();
		Map<MLKAgent, Pair<Integer, Integer>> previousPositions = state.getAgentsPositions();
		Map<MLKAgent, Pair<Integer, Integer>> candidatePositions = computeCandidatePositions(actions, previousPositions);
		Map<MLKAgent, Pair<Integer, Integer>> resolvedPositions = resolveTargetConflicts(previousPositions, candidatePositions);
		applyResolvedPositions(previousPositions, resolvedPositions);
		buildEvents(reactionEvents, resolvedPositions);
		return reactionEvents;
	}

	private Map<MLKAgent, Pair<Integer, Integer>> computeCandidatePositions(
			Map<MLKAgent, Action> actions,
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions) {

		Map<MLKAgent, Pair<Integer, Integer>> candidates = new HashMap<>();
		for (MLKAgent agent : agents.getAgents()) {
			Pair<Integer, Integer> previous = previousPositions.get(agent).clone();
			if (escapedAgents.contains(agent)) {
				candidates.put(agent, previous);
				continue;
			}

			Action action = actions.get(agent);
			if (!(action instanceof Move2D moveAction)) {
				candidates.put(agent, previous);
				continue;
			}

			Pair<Integer, Integer> move = moveAction.getValue();
			Pair<Integer, Integer> target = new Pair<>(
					previous.getFirst() + move.getFirst(),
					previous.getSecond() + move.getSecond());

			if (!isInsideGrid(target) || !isCrossCell(target.getFirst(), target.getSecond())) {
				candidates.put(agent, previous);
				continue;
			}

			if (isOccupiedInPreviousState(target, previousPositions)) {
				candidates.put(agent, previous);
				continue;
			}

			candidates.put(agent, target);
		}
		return candidates;
	}

	private Map<MLKAgent, Pair<Integer, Integer>> resolveTargetConflicts(
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions,
			Map<MLKAgent, Pair<Integer, Integer>> candidatePositions) {

		Map<MLKAgent, Pair<Integer, Integer>> resolved = new HashMap<>();
		Map<Pair<Integer, Integer>, List<MLKAgent>> groupedByTarget = new HashMap<>();

		for (MLKAgent agent : agents.getAgents()) {
			Pair<Integer, Integer> target = candidatePositions.get(agent);
			groupedByTarget.computeIfAbsent(target, key -> new ArrayList<>()).add(agent);
		}

		for (Map.Entry<Pair<Integer, Integer>, List<MLKAgent>> entry : groupedByTarget.entrySet()) {
			List<MLKAgent> contenders = entry.getValue();
			if (contenders.size() == 1) {
				MLKAgent onlyAgent = contenders.get(0);
				resolved.put(onlyAgent, candidatePositions.get(onlyAgent));
				continue;
			}

			MLKAgent winner = contenders.get(prng().nextInt(contenders.size()));
			for (MLKAgent contender : contenders) {
				if (contender.equals(winner)) {
					resolved.put(contender, candidatePositions.get(contender));
				} else {
					resolved.put(contender, previousPositions.get(contender).clone());
				}
			}
		}

		return resolved;
	}

	private void applyResolvedPositions(
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions,
			Map<MLKAgent, Pair<Integer, Integer>> resolvedPositions) {

		for (MLKAgent agent : agents.getAgents()) {
			Pair<Integer, Integer> previous = previousPositions.get(agent);
			Pair<Integer, Integer> resolved = resolvedPositions.get(agent);
			Pair<Integer, Integer> delta = new Pair<>(
					resolved.getFirst() - previous.getFirst(),
					resolved.getSecond() - previous.getSecond());
			state.moveAgent(agent, delta);
		}
	}

	private void buildEvents(Map<MLKAgent, List<ReactionEvent>> reactionEvents,
			Map<MLKAgent, Pair<Integer, Integer>> resolvedPositions) {
		for (MLKAgent agent : agents.getAgents()) {
			List<ReactionEvent> events = new ArrayList<>();
			reactionEvents.put(agent, events);

			if (escapedAgents.contains(agent)) {
				events.add(new NothingEvent());
				continue;
			}

			Pair<Integer, Integer> goal = goalPositions.get(agent);
			Pair<Integer, Integer> current = resolvedPositions.get(agent);
			if (goal.equals(current)) {
				escapedAgents.add(agent);
				state.setValue(goal, GOAL_REACHED_MARKER);
				events.add(new EscapedEvent());
			} else {
				events.add(new NotEscapedYetEvent());
			}
		}
	}

	private boolean isOccupiedInPreviousState(Pair<Integer, Integer> target,
			Map<MLKAgent, Pair<Integer, Integer>> previousPositions) {
		for (Pair<Integer, Integer> position : previousPositions.values()) {
			if (position.equals(target)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInsideGrid(Pair<Integer, Integer> position) {
		return position.getFirst() >= 0
				&& position.getFirst() < getWidth()
				&& position.getSecond() >= 0
				&& position.getSecond() < getHeight();
	}

	private boolean isCrossCell(int x, int y) {
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		return x == centerX || y == centerY;
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions() {
		return state.getAgentsPositions();
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getStartPositions() {
		return new HashMap<>(startPositions);
	}

	public Map<MLKAgent, Pair<Integer, Integer>> getGoalPositions() {
		return new HashMap<>(goalPositions);
	}

	public int getEscapedAgentsCount() {
		return escapedAgents.size();
	}

	@Override
	public State2DGridInt getState() {
		return state;
	}
}
