package marlkit.maze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2D;
import environment.EnvironmentStandard;
import environment.state.State2DGridInt;
import marlkit.maze.events.MazeExitEvent;
import marlkit.maze.events.MazeHoleEvent;
import marlkit.maze.events.MazeStepEvent;
import rewardmodeling.ReactionEvent;
import rewardmodels.MixedReward;
import util.Pair;

public class EnvMazeEscape extends EnvironmentStandard {

	protected State2DGridInt state;
	private MazeScenario scenario;
	private MazeRewardConfig rewardConfig;
	private boolean terminalReached;

	public EnvMazeEscape() {
		this(MazeRewardConfig.DEFAULT);
	}

	public EnvMazeEscape(MazeRewardConfig rewardConfig) {
		this(MazeScenarios.defaultMaze(), rewardConfig);
	}

	public EnvMazeEscape(MazeScenario scenario, MazeRewardConfig rewardConfig) {
		super(scenario.getWidth(), scenario.getHeight(), new MixedReward());
		this.scenario = scenario;
		this.rewardConfig = rewardConfig;
		this.terminalReached = false;
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
		scenario.applyTo(state);
		terminalReached = false;
	}

	@Override
	public void setupAgents() {
		Pair<Integer, Integer> spawn = scenario.getSpawn();
		for (MLKAgent ag : agents.getAgents()) {
			state.addAgent(ag, spawn.getFirst(), spawn.getSecond());
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
		Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();
		for (MLKAgent ag : agents.getAgents()) {
			List<ReactionEvent> events = new ArrayList<>();
			results.put(ag, events);
			if (terminalReached) {
				continue;
			}
			Move2D action = (Move2D) actions.get(ag);
			Pair<Integer, Integer> newPosition = stateMoveAgent(ag, action);
			events.add(new MazeStepEvent(rewardConfig.stepReward()));
			handleCellEvents(newPosition, events);
		}
		return results;
	}

	protected Pair<Integer, Integer> stateMoveAgent(MLKAgent agent, Move2D action) {
		Pair<Integer, Integer> currentPosition = state.getAgentPosition(agent).clone();
		int nextX = Math.max(0, Math.min(getWidth() - 1, currentPosition.getFirst() + action.getValue().getFirst()));
		int nextY = Math.max(0, Math.min(getHeight() - 1, currentPosition.getSecond() + action.getValue().getSecond()));
		Pair<Integer, Integer> candidatePosition = new Pair<>(nextX, nextY);

		if (scenario.getCellType(candidatePosition) == MazeCellType.WALL) {
			return currentPosition;
		}

		state.moveAgent(agent, action.getValue());
		return state.getAgentPosition(agent).clone();
	}

	private void handleCellEvents(Pair<Integer, Integer> position, List<ReactionEvent> events) {
		MazeCellType cellType = scenario.getCellType(position);
		if (cellType == MazeCellType.HOLE) {
			events.add(new MazeHoleEvent(rewardConfig.holeReward()));
			terminalReached = true;
		} else if (cellType == MazeCellType.EXIT) {
			events.add(new MazeExitEvent(rewardConfig.exitReward()));
			terminalReached = true;
		}
	}

	public boolean isTerminalReached() {
		return terminalReached;
	}

	public MazeScenario getScenario() {
		return scenario;
	}

	public void setScenario(MazeScenario scenario) {
		this.scenario = scenario;
	}

	public MazeRewardConfig getRewardConfig() {
		return rewardConfig;
	}

	public void setRewardConfig(MazeRewardConfig rewardConfig) {
		this.rewardConfig = rewardConfig;
	}

	@Override
	public State2DGridInt getState() {
		return state;
	}
}
