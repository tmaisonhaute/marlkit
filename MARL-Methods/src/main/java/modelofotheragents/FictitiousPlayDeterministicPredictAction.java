package modelofotheragents;

import java.util.HashMap;
import java.util.Map;

import agent.action.Action;
import agent.modelofotheragent.ModelPredictAction;
import learning.policy.PolicyInput;

public class FictitiousPlayDeterministicPredictAction implements ModelPredictAction {
	protected Map<PolicyInput, ActionsFrequencies> actionsFrequenciesByInput;
	protected Action defaultAction;
	protected Action lastPredictedAction;
	
	public FictitiousPlayDeterministicPredictAction(Action defaultAction) {
		this.actionsFrequenciesByInput = new HashMap<>();
		this.defaultAction = defaultAction;
	}

	@Override
	public Action predictAction(PolicyInput observation) {
		ActionsFrequencies frequencies = this.actionsFrequenciesByInput.get(observation);
		if (frequencies == null) {
			return defaultAction.copy();
		}
		Action mostFrequent = frequencies.getMostFrequentAction();
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
		ActionsFrequencies frequencies = this.actionsFrequenciesByInput.get(observation);
		if (frequencies == null) {
			frequencies = new ActionsFrequencies();
			this.actionsFrequenciesByInput.put(observation, frequencies);
		}
		frequencies.addAction(actualAction);
	}

	@Override
	public Action getLastPredictedJointAction() {
		return lastPredictedAction;
	}

}

class ActionsFrequencies{
	Map<Action, Integer> frequencies;

	public ActionsFrequencies() {
		this.frequencies = new HashMap<>();
	}

	public void addAction(Action action) {
		this.frequencies.put(action.copy(), getFrequency(action) + 1);
	}
	
	public int getFrequency(Action action) {
		return this.frequencies.getOrDefault(action, 0);
	}
	
	public Action getMostFrequentAction() {
		Action mostFrequent = null;
		int maxFrequency = -1;
		for (var entry : frequencies.entrySet()) {
			if (entry.getValue() > maxFrequency) {
				maxFrequency = entry.getValue();
				mostFrequent = entry.getKey();
			}
		}
		if (mostFrequent == null) {
			return null;
		}
		return mostFrequent.copy();
	}
}