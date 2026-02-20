package learning.algorithm;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import learning.Batch;
import learning.Experience;
import learning.policy.Policy;
import learning.policy.PolicyInput;
import learning.policy.QValueBasedPolicy;
import madkit.kernel.AgentLogger;
import util.Pair;

public class MonteCarlo implements Algorithm {
	
	private final double gamma;
    private Map<Pair<PolicyInput, Action>, Integer> nbSelected;
    private MLKAgent agent;
    private QValueBasedPolicy policy;
    
    
    /**
     * Creates a MonteCarlo algorithm with specified parameters.
     *
     * @param policy     the Q-value based policy to use and update
     * @param gamma      the discount factor
     */
    public MonteCarlo(QValueBasedPolicy policy, double gamma) {
    	this.policy = policy;
        this.gamma = gamma;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(MLKAgent agent) {
        this.agent = agent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPolicy(Policy policy) {
        if (policy instanceof QValueBasedPolicy qPolicy) {
            this.policy = qPolicy;
        } else {
            throw new IllegalArgumentException("SARSA requires a QValueBasedPolicy");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QValueBasedPolicy getPolicy() {
        return this.policy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MLKAgent getAgent() {
        return this.agent;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns 0, meaning the algorithm do not learn during an episode.
     * </p>
     */
    @Override
    public int getLearningFrequency() {
        return 0;
    }
    

	/**
	 * Update Q-values based on experiences and their cumulative rewards.
	 * 
	 * @param experiences
	 * @param cumulRewards
	 */
    protected void updateQ(List<Experience> experiences, double[] cumulRewards) {
		int experiencesLength = experiences.size();
	    for (int k = 0; k < experiencesLength; k++) {
	        Pair<PolicyInput, Action> stateAction = createStateAction(experiences.get(k));
	        double cumulativeReward = cumulRewards[k];
	
	        double averageReward = getPolicy().getTable().getValue(stateAction);
	        int nb = nbSelected.getOrDefault(stateAction, 0);
	
	        getPolicy().getTable().setValue(stateAction, averageReward * nb / (nb + 1) + cumulativeReward * 1 / (nb + 1));
	        nbSelected.put(stateAction, nb + 1);
	    }
	}
    
    /**
     * Create a state-action pair from an experience.
     * @param experience
     * @return
     */
    protected Pair<PolicyInput, Action> createStateAction(Experience experience) {
		PolicyInput state = experience.getInput();
		Action action = experience.getAction();
		return new Pair<>(state, action);
	}



    /*
     * * {@inheritDoc}
     */
	@Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
		List<Experience> experiences = batch.getExperiences();
		double[] cumulativeRewards = batch.computeCumulativeRewards(gamma);
		logger.info("Total rewards: " + batch.totalRewards());
		updateQ(experiences, cumulativeRewards);
		
		policy.getExplorationStrategy().update();
		
	}

	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
		learnOnBatch(batch, logger);
		logger.info("size of batch : " + batch.getExperiences().size());
        logger.info(policy.getExplorationStrategy().getLoggerInfo());
        logger.info("size Q: " + getPolicy().getTable().size());
		batch.clear();
	}

}
