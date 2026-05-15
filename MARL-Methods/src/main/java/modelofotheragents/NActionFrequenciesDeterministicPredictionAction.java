package modelofotheragents;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import agent.action.Action;
import agent.modelofotheragent.ModelPredictAction;
import learning.policy.PolicyInput;

/**
 * Deterministic prediction based on the last N actions per state.
 */
public class NActionFrequenciesDeterministicPredictionAction implements ModelPredictAction {
	protected Map<PolicyInput, ActionsWindow> actionsByInput;
	protected Action defaultAction;
	protected Action lastPredictedAction;
	protected int windowSize;

	public NActionFrequenciesDeterministicPredictionAction(Action defaultAction, int windowSize) {
		if (windowSize <= 0) {
			throw new IllegalArgumentException("windowSize must be positive.");
		}
		this.actionsByInput = new HashMap<>();
		this.defaultAction = defaultAction;
		this.windowSize = windowSize;
	}

	@Override
	public Action predictAction(PolicyInput observation) {
		ActionsWindow window = this.actionsByInput.get(observation);
		if (window == null) {
			return defaultAction.copy();
		}
		Action mostFrequent = window.getMostFrequentAction();
		if (mostFrequent == null) {
			mostFrequent = defaultAction.copy();
		}
		this.lastPredictedAction = mostFrequent.copy();
		return mostFrequent;
	}

	@Override
	public Action predictAction(PolicyInput observation, Action action) {
		return predictAction(observation);
	}

	@Override
	public void updateModel(PolicyInput observation, Action predictedAction, Action actualAction) {
		ActionsWindow window = this.actionsByInput.get(observation);
		if (window == null) {
			window = new ActionsWindow(windowSize);
			this.actionsByInput.put(observation, window);
		}
		window.addAction(actualAction);
	}

	@Override
	public Action getLastPredictedJointAction() {
		return lastPredictedAction;
	}
}

class ActionsWindow {
	private final Deque<Action> actions;
	private final int windowSize;

	public ActionsWindow(int windowSize) {
		this.windowSize = windowSize;
		this.actions = new ArrayDeque<>();
	}

	public void addAction(Action action) {
		actions.addLast(action.copy());
		while (actions.size() > windowSize) {
			actions.removeFirst();
		}
	}

	public Action getMostFrequentAction() {
		if (actions.isEmpty()) {
			return null;
		}
		Map<Action, Integer> freq = new HashMap<>();
		Action mostFrequent = null;
		int maxFrequency = -1;
		for (Action action : actions) {
			int count = freq.getOrDefault(action, 0) + 1;
			freq.put(action, count);
			if (count > maxFrequency) {
				maxFrequency = count;
				mostFrequent = action;
			}
		}
		return mostFrequent == null ? null : mostFrequent.copy();
	}
}
