
package learning.policy;


import java.util.List;

import agent.AgentStandard;
import agent.MLKAgent;
import agent.action.Action;
import agent.action.wrapperactionvector.WrapperAction2DMoveVector;
import agent.action.wrapperactionvector.WrapperActionVector;
import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;
import environment.observation.wrapperobservationvector.WrapperObservationVectorPositionsValues;
import learning.Batch;
import learning.Experience;
import learning.nn.ActorNetwork;
import madkit.kernel.AgentLogger;

/**
 * Policy Gradient reinforcement learning policy.
 * <p>
 * This policy-based method directly optimizes the policy by computing gradients
 * from complete episode trajectories. It uses cumulative discounted rewards
 * to update the actor network at the end of each episode.
 * </p>
 * <p>
 * Unlike Actor-Critic, this method does not use a value function baseline,
 * which may result in higher variance but simpler implementation.
 * </p>
 *
 * @see ActorNetwork
 * @see Policy
 */
public class PolicyGradient implements Policy {

    private AgentStandard agent;

    private ActorNetwork neuralNetwork;

    private int inputSize;
    private int hiddenSize;
    private List<Action> actionSet;
    private double gamma;
    private WrapperObservationVector observationWrapper;
    private WrapperActionVector actionWrapper;

    /**
     * Creates a Policy Gradient policy with the specified action set and input size.
     * <p>
     * Uses default wrappers for observation and action conversion,
     * a discount factor (gamma) of 0.95, and a hidden layer size of 64.
     * </p>
     *
     * @param actionSet the list of possible actions the agent can take
     * @param inputSize the size of the observation vector (neural network input)
     */
    public PolicyGradient(List<Action> actionSet, int inputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = 64;
        this.actionSet = actionSet;
        this.gamma = 0.95;

        this.observationWrapper = new WrapperObservationVectorPositionsValues(false);
        this.actionWrapper = new WrapperAction2DMoveVector();
    }
    @Override
    public int getLearningFrequency() {
         return 0;
    }


    @Override
    /**
     * Initializes the policy with the given agent.
     * <p>
     * Sets up the actor neural network for action selection and learning.
     * </p>
     */
    public void init(MLKAgent agent) {
        this.agent = (AgentStandard) agent;
        this.neuralNetwork = new ActorNetwork(inputSize, hiddenSize,pnrg(),0.001,observationWrapper, actionWrapper,actionSet);
    }

    @Override
    /**
     * Selects an action based on the current observation using the actor network.
     * <p>
     * The neural network outputs a probability distribution over actions,
     * from which an action is sampled.
     * </p>
     * @return the selected action
     */
    public Action takeAction(Observation observation) {
        return neuralNetwork.selectAction(observation);
    }

    @Override
    public AgentStandard getAgent() {
        return agent;
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

    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        List<Experience> experiences = batch.getExperiences();
        double[] cumulativeRewards = computeCumulativeRewards(experiences, logger);
        for (int i = 0; i <= experiences.size()-1; i++) {
            Experience experience = experiences.get(i);
            neuralNetwork.update(experience.getObservation(), experience.getAction(), cumulativeRewards[i]);
        }
        batch.clear();
    }

    @Override
    public void endEpisode(Batch batch, AgentLogger logger) {
        learnOnBatch(batch, logger);
    }
}


