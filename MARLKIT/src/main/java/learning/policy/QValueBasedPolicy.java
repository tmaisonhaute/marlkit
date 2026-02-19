package learning.policy;

import java.util.List;
import java.util.Optional;

import agent.MLKAgent;
import agent.action.Action;
import learning.policy.explorationsettings.EpsilonGreedy;
import learning.policy.explorationsettings.ExplorationStrategy;
import learning.valuefunction.QTable;
import util.Pair;

public class QValueBasedPolicy implements Policy{

	protected QTable qTable;
	private ExplorationStrategy explorationStrategy;
	private MLKAgent agent;
	private List<Action> actionsSet;
	
	public QValueBasedPolicy(List<Action> actionsSet) {
		this(actionsSet, 0.0);
	}
	
	public QValueBasedPolicy(List<Action> actionsSet, double defaultValue) {
		this(actionsSet, defaultValue, new EpsilonGreedy());
	}
	
	public QValueBasedPolicy(List<Action> actionsSet, double defaultValue, ExplorationStrategy explorationStrategy) {
		this.actionsSet = actionsSet;
		this.qTable = new QTable(defaultValue);
		this.explorationStrategy = explorationStrategy;
	}
	
	
	@Override
	public void init(MLKAgent agent) {
		this.agent = agent;
	}
	
	
	public void reset() {
		getTable().reset();
	}

	@Override
	public MLKAgent getAgent() {
		return agent;
	}

	@Override
	public Action takeAction(PolicyInput input) {
		Optional<Action> exploratoryAction = getExplorationStrategy().getExploratoryAction(actionsSet, pnrg());
		if (!exploratoryAction.isEmpty()) {
			return exploratoryAction.get();
		}
        Action selectedAction = null;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (Action act : actionsSet) {
            Pair<PolicyInput, Action> newStateAction = new Pair<>(input, act);
            double qval = getTable().getValue(newStateAction);
            if(qval > maxVal) {
            	selectedAction = act;
            	maxVal = qval;
            }
        }
		return selectedAction;
	}
	
	public QTable getTable() {
		return qTable;
	}
	
	@Override
	public ExplorationStrategy getExplorationStrategy() {
		return explorationStrategy;
	}

}
