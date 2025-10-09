package learning.policy.valuebased;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.policy.PolicyEpsilon;
import madkit.kernel.AgentLogger;
import util.Pair;

public abstract class PolicyQValueBased extends PolicyEpsilon {

	private Map<Pair<Observation, Action>, Double> q;
	private List<Action> actionsSet;
	
	
	protected PolicyQValueBased(List<Action> actionsSet, double epsilon, double epsilonDecrease) {
        super(epsilon, epsilonDecrease);
        
        this.actionsSet = actionsSet;
        this.q = new HashMap<>();
    }

	protected PolicyQValueBased(List<Action> actionsSet, double epsilon) {
        this(actionsSet, epsilon, 0.0);
    }

	protected PolicyQValueBased(List<Action> actionsSet) {
        this(actionsSet, 0.05);
    }
	
	@Override
	public void init(MLKAgent agent) {
		this.q = new HashMap<>();
	}

	@Override
	public MLKAgent getAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLearningFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
    public Action takeAction(Observation observation) {
		
		RandomGenerator random = pnrg();
        if (random.nextDouble() < getEpsilon()) {
            return actionsSet.get(random.nextInt(actionsSet.size()));
        }

        Action selectedAction = null;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (Action act : actionsSet) {
            Pair<Observation, Action> newStateAction = new Pair<>(observation, act);
            if (!q.containsKey(newStateAction)) {
                return act;
            }

            if (q.get(newStateAction) > maxVal) {
                selectedAction = act;
                maxVal = q.get(newStateAction);
            }
        }

        return selectedAction;
    }

	@Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
		// TODO Auto-generated method stub

	}
	
	public Map<Pair<Observation, Action>, Double> getQ() {
        return q;
    }
	
	protected List<Action> getActionsSet(){
        return actionsSet;
    }
	
	
	

}
