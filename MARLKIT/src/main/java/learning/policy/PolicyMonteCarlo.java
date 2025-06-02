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

public class PolicyMonteCarlo extends PolicyEpsilon {
	
	private MLKAgent agent;
	private Map<Pair<Observation, Action>, Double> q;
    private Map<Pair<Observation, Action>, Integer> nbSelected;
    private List<Action> actionsSet;
    private int numberOfActions;
    private double gamma;

    
	public PolicyMonteCarlo(List<Action> actionsSet, double epsilon, double epsilonDecrease) {
		super(epsilon, epsilonDecrease);
		this.actionsSet = actionsSet;
		this.numberOfActions = actionsSet.size();
		this.gamma = 0.95;
		
		this.q = new HashMap<>();
		this.nbSelected = new HashMap<>();
	}
    
    public PolicyMonteCarlo(List<Action> actionsSet, double epsilon) {
    	this(actionsSet, epsilon, 0.0);
    }

	public PolicyMonteCarlo(List<Action> actionsSet) {
		this(actionsSet, 0.05);
	}
	

	@Override
	public void init(MLKAgent agent) {
		this.agent = agent;
		this.q = new HashMap<>();
		this.nbSelected = new HashMap<>();
		
	}



	@Override
	public int getLearningFrequency() {
		return 0;
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

    
    protected double[] computeCumulativeRewards(List<Experience> experiences, AgentLogger logger) {
    	int experiencesLength = experiences.size();
    	double[] cumulativeRewards = new double[experiencesLength];
    	double totalRewards = 0;
    	for (int j = experiencesLength - 1; j >= 0; j--) {
    		cumulativeRewards[j] = experiences.get(j).getRewardValue() + (j + 1 < experiencesLength ? cumulativeRewards[j + 1] * gamma : 0);
    		totalRewards += experiences.get(j).getRewardValue();
    	}
    	logger.info("Total rewards: " + totalRewards);
    	return cumulativeRewards;
    }

	protected void updateQ(List<Experience> experiences, double[] cumulRewards) {
		int experiencesLength = experiences.size();
	    for (int k = 0; k < experiencesLength; k++) {
	        Pair<Observation, Action> stateAction = createStateAction(experiences.get(k));
	        double cumulativeReward = cumulRewards[k];
	
	        double averageReward = q.getOrDefault(stateAction, 0.0);
	        int nb = nbSelected.getOrDefault(stateAction, 0);
	
	        q.put(stateAction, averageReward * nb / (nb + 1) + cumulativeReward * 1 / (nb + 1));
	        nbSelected.put(stateAction, nb + 1);
	    }
	}

	protected Pair<Observation, Action> createStateAction(Experience experience) {
		Observation state = experience.getObservation();
		Action action = experience.getAction();
		return new Pair<>(state, action);
	}

	@Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
		List<Experience> experiences = batch.getExperiences();
		double[] cumulativeRewards = computeCumulativeRewards(experiences, logger);
		updateQ(experiences, cumulativeRewards);
		
		updateEpsilon();
		batch.clear();
	}
	
	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
		learnOnBatch(batch, logger);
		logger.info("size of batch : " + batch.getExperiences().size());
	    logger.info("size Q: " + q.size());
	    logger.info("Epsilon : " + getEpsilon());
	}


	@Override
	public MLKAgent getAgent() {
		return agent;
	}



	

	
	
}
