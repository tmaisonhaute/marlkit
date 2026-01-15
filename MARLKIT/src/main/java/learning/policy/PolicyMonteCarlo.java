package learning.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import learning.Experience;
import learning.policy.valuebased.PolicyQValueBased;
import madkit.kernel.AgentLogger;
import util.Pair;

/**
 * Monte Carlo reinforcement learning policy with epsilon-greedy exploration.
 * <p>
 * This value-based method learns Q-values by averaging returns observed after
 * visiting state-action pairs. Learning occurs at the end of each episode
 * using complete trajectories.
 * </p>
 * <p>
 * Uses incremental mean updates to efficiently track average returns without
 * storing all historical values.
 * </p>
 *
 * @see PolicyQValueBased
 * @see PolicyEpsilon
 */
public class PolicyMonteCarlo extends PolicyQValueBased {
	
	private MLKAgent agent;
    private Map<Pair<Observation, Action>, Integer> nbSelected;
    private int numberOfActions;
    private double gamma;

    /**
     * Creates a Monte Carlo policy with epsilon-greedy exploration and decay.
     *
     * @param actionsSet      the list of possible actions
     * @param epsilon         the initial exploration rate
     * @param epsilonDecrease the rate at which epsilon decreases per episode
     */
	public PolicyMonteCarlo(List<Action> actionsSet, double epsilon, double epsilonDecrease) {
		super(actionsSet, epsilon, epsilonDecrease);
		this.numberOfActions = actionsSet.size();
		this.gamma = 0.95;
		
		this.nbSelected = new HashMap<>();
	}

    /**
     * Creates a Monte Carlo policy with epsilon-greedy exploration (no decay).
     *
     * @param actionsSet the list of possible actions
     * @param epsilon    the exploration rate
     */
    public PolicyMonteCarlo(List<Action> actionsSet, double epsilon) {
    	this(actionsSet, epsilon, 0.0);
    }

    /**
     * Creates a Monte Carlo policy with default epsilon of 0.05.
     *
     * @param actionsSet the list of possible actions
     */
	public PolicyMonteCarlo(List<Action> actionsSet) {
		this(actionsSet, 0.05);
	}
	

	@Override
	public void init(MLKAgent agent) {
		super.init(agent);
		this.agent = agent;
		this.nbSelected = new HashMap<>();
		
	}


	/**
	 * {@inheritDoc}
	 * Returns 0 since Monte Carlo learns only at episode end.
	 */
	@Override
	public int getLearningFrequency() {
		return 0;
	}

    /**
     * Computes discounted cumulative rewards for each step in the episode.
     * <p>
     * For each timestep t, computes G_t = r_t + gamma * G_{t+1}, working backwards
     * from the end of the episode.
     * </p>
     *
     * @param experiences the list of experiences from the episode
     * @param logger      the logger for outputting total rewards
     * @return an array of cumulative rewards, one per experience
     */
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

    /**
     * Updates Q-values using incremental mean calculation.
     * <p>
     * For each state-action pair, updates the Q-value as:
     * Q(s,a) = Q(s,a) * n/(n+1) + G * 1/(n+1)
     * where n is the visit count and G is the cumulative reward.
     * </p>
     *
     * @param experiences  the list of experiences from the episode
     * @param cumulRewards the cumulative rewards for each experience
     */
	protected void updateQ(List<Experience> experiences, double[] cumulRewards) {
		int experiencesLength = experiences.size();
	    for (int k = 0; k < experiencesLength; k++) {
	        Pair<Observation, Action> stateAction = createStateAction(experiences.get(k));
	        double cumulativeReward = cumulRewards[k];
	
	        double averageReward = getQ().getOrDefault(stateAction, 0.0);
	        int nb = nbSelected.getOrDefault(stateAction, 0);
	
	        getQ().put(stateAction, averageReward * nb / (nb + 1) + cumulativeReward * 1 / (nb + 1));
	        nbSelected.put(stateAction, nb + 1);
	    }
	}

	/**
	 * Creates a state-action pair from an experience.
	 * @param experience
	 * @return the state-action pair
	 */
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
	    logger.info("size Q: " + getQ().size());
	    logger.info("Epsilon : " + getEpsilon());
	}


	@Override
	public MLKAgent getAgent() {
		return agent;
	}




	

	
	
}
