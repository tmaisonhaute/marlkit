package learning.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.Experience;
import madkit.kernel.AgentLogger;
import util.Pair;

public class PolicyMonteCarlo implements Policy {
	
	private Map<Pair<Observation, Action>, Double> Q;
    private Map<Pair<Observation, Action>, Integer> nbSelected;
    private List<Action> actionsSet;
    private int numberOfActions;
    private double gamma;
    private double eps;
    private Random random;

    public PolicyMonteCarlo(List<Action> actionsSet) {
        this.Q = new HashMap<>();
        this.nbSelected = new HashMap<>();
        this.actionsSet = actionsSet;
        this.numberOfActions = actionsSet.size();
        this.gamma = 0.95;
        this.eps = 0.05;
        this.random = new Random();
    }

    @Override
    public Action takeAction(Observation obs) {
        if (random.nextDouble() < eps) {
            return actionsSet.get(random.nextInt(numberOfActions));
        }

        Action selectedAction = null;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (Action act : actionsSet) {
            Pair<Observation, Action> newStateAction = new Pair<>(obs, act);
            if (!Q.containsKey(newStateAction)) {
                return act;
            }

            if (Q.get(newStateAction) > maxVal) {
                selectedAction = act;
                maxVal = Q.get(newStateAction);
            }
        }

        return selectedAction;
    }

    @Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
	    List<Experience> experiences = batch.getExperiences();
	
	    int rewardLen = experiences.size();
	    double[] cumulRewards = new double[rewardLen];
	    double totRewards = 0;
	    for (int j = rewardLen - 1; j >= 0; j--) {
	        cumulRewards[j] = experiences.get(j).getRewardValue() + (j + 1 < rewardLen ? cumulRewards[j + 1] * gamma : 0);
	        totRewards += experiences.get(j).getRewardValue();
	    }
	
	    for (int k = 0; k < rewardLen; k++) {
	        Observation state = experiences.get(k).getObservation();
	        Action action = experiences.get(k).getAction();
	        double cumulReward = cumulRewards[k];
	        Pair<Observation, Action> stateAction = new Pair<>(state, action);
	
	        double rewardMoyen = Q.getOrDefault(stateAction, 0.0);
	        int nb = nbSelected.getOrDefault(stateAction, 0);
	
	        Q.put(stateAction, rewardMoyen * nb / (nb + 1) + cumulReward * 1 / (nb + 1));
	        nbSelected.put(stateAction, nb + 1);
	    }
	    logger.info("Total rewards: " + totRewards);
	    logger.info("size Q: " + Q.size());
	}

	
	
}
