
package learning.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.Experience;
import madkit.kernel.AgentLogger;
import util.Pair;

public class PolicySarsa extends PolicyEpsilon {

	private MLKAgent agent;
    private Map<Pair<Observation, Action>, Double> q;
    private List<Action> actionsSet;
    private int numberOfActions;
    private double gamma;
    private double alpha;
    private double totalRewards;

    public PolicySarsa(List<Action> actionsSet, double epsilon, double epsilonDecrease) {
        super(epsilon, epsilonDecrease);
        this.actionsSet = actionsSet;
        this.numberOfActions = actionsSet.size();
        this.gamma = 0.95;
        this.alpha = 0.1;
        this.totalRewards = 0.0;

        this.q = new HashMap<>();
    }

    public PolicySarsa(List<Action> actionsSet, double epsilon) {
        this(actionsSet, epsilon, 0.0);
    }

    public PolicySarsa(List<Action> actionsSet) {
        this(actionsSet, 0.05);
    }
    

	@Override
	public void init(MLKAgent agent) {
		this.agent = agent;
        this.q = new HashMap<>();
	}

    @Override
	public int getLearningFrequency() {
        return 1;
    }
      
    public Map<Pair<Observation, Action>, Double> getQ() {
        return q;
    }

    @Override
    public Action takeAction(Observation observation) {
    	RandomGenerator random = pnrg();
        if (random.nextDouble() < getEpsilon()) {
            return actionsSet.get(random.nextInt(numberOfActions));
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

    protected void update(Observation state, Action action, double reward, Observation nextState, Action nextAction) {
        double qNext = 0;
        if (nextState != null && nextAction != null) {
            Pair<Observation, Action> nextStateAction = new Pair<>(nextState, nextAction);
            qNext = q.getOrDefault(nextStateAction, 0.0);
        }

        Pair<Observation, Action> stateAction = new Pair<>(state, action);
        double qValue = q.getOrDefault(stateAction, 0.0);
        q.put(stateAction, qValue + alpha * (reward + gamma * qNext - qValue));
    }

    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
    	while (batch.getExperiences().size() > 1) {
            totalRewards += learnOneStep(batch);
        }
    }

	protected double learnOneStep(Batch batch) {
		Experience currentExperience = batch.getExperiences().get(0);
		Experience nextExperience = batch.getExperiences().get(1);

		Observation currentState = currentExperience.getObservation();
		Action currentAction = currentExperience.getAction();
		double reward = currentExperience.getRewardValue();
		Observation nextState = nextExperience.getObservation();
		Action nextAction = nextExperience.getAction();

		update(currentState, currentAction, reward, nextState, nextAction);
		batch.getExperiences().remove(0);
		return reward;
	}

	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
		learnOnBatch(batch, logger);
        if (batch.getExperiences().size() == 1) {
            Experience lastExperience = batch.getExperiences().get(0);
            Observation lastState = lastExperience.getObservation();
            Action lastAction = lastExperience.getAction();
            double lastReward = lastExperience.getRewardValue();

            update(lastState, lastAction, lastReward, null, null);
            batch.getExperiences().remove(0);
            totalRewards += lastReward;
        }
        updateEpsilon();
        logger.info("total rewards : " + totalRewards);
	    logger.info("size Q: " + q.size());
	    logger.info("Epsilon : " + getEpsilon());
	    totalRewards = 0.0;
	}


	@Override
	public MLKAgent getAgent() {
		return agent;
	}
    
}
