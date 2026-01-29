package learning.algorithm;

import agent.MLKAgent;
import agent.action.Action;
import learning.Batch;
import learning.nn.ActorNetwork;
import learning.policy.Policy;
import learning.policy.PolicyInput;
import madkit.kernel.AgentLogger;

public class Reinforce implements Algorithm {
	private ActorNetwork policy;
	private MLKAgent agent;
	private double alpha;
	private double gamma;
	
    public Reinforce(ActorNetwork policy, double alpha, double gamma) {
        this.policy = policy;
        this.gamma = gamma;
        this.alpha = alpha;
    }

	@Override
	public void init(MLKAgent agent) {
		this.agent = agent;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public void setPolicy(Policy policy) {
        if (policy instanceof ActorNetwork an) {
            this.policy = an;
        } else {
            throw new IllegalArgumentException("Reinforce requires a ActorNetwork policy");
        }
    }

	@Override
	public Policy getPolicy() {
		return policy;
	}

	@Override
	public MLKAgent getAgent() {
		return agent;
	}

	@Override
	public int getLearningFrequency() {
		return 0;
	}

	@Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
		PolicyInput[] inputBatch = batch.getAllInputs();
		Action[] actionBatch = batch.getAllActions();
		double[][] logitsBatch = policy.forwardLogits(inputBatch);
		double[][] probsBatch  = policy.softmax(logitsBatch);
		
	
		int[] actionIndexBatch = toActionIndices(actionBatch);
	    double[] weightBatch = batch.computeCumulativeRewards(gamma);
	    logger.info("Total rewards: " + batch.totalRewards());
	
	
	    double[][] dLossDLogits = computePolicyGradientSignal(probsBatch, actionIndexBatch, weightBatch);
	
	    policy.updateFromLogitsGradient(inputBatch, dLossDLogits, alpha);
	
	    
	}
	
	/**
	 * Compute dL/dLogits for the batch using the policy gradient theorem. L being the loss function.
	 * @param probsBatch neural network output probabilities for the batch
	 * @param actionIndexBatch indices of the actions taken in the batch
	 * @param weightBatch weights for each sample in the batch (Gain_t)
	 * @return dL/dLogits for the batch
	 */
	private double[][] computePolicyGradientSignal(double[][] probsBatch, int[] actionIndexBatch,
			double[] weightBatch) {
		double[][] dLossDLogits = new double[probsBatch.length][probsBatch[0].length];
		for (int i = 0; i < probsBatch.length; i++) {
			double weight = weightBatch[i];
			for (int j = 0; j < probsBatch[i].length; j++) {
				dLossDLogits[i][j] = probsBatch[i][j] * weight;
			}
			dLossDLogits[i][actionIndexBatch[i]] -= weight;
		}
		return dLossDLogits;
	}
	
	/**
	 * Converts an array of actions to their corresponding indices in the actions set.
	 * @param actionBatch
	 * @return
	 */
	private int[] toActionIndices(Action[] actionBatch) {
		int[] actionIndices = new int[actionBatch.length];
		for (int i = 0; i < actionBatch.length; i++) {
			actionIndices[i] = policy.getActionSet().indexOf(actionBatch[i]);
		}
		return actionIndices;
	}
	
	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
		learnOnBatch(batch, logger);
		batch.clear();

	}

}
