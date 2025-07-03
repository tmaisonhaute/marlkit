
package learning.policy;


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


import java.util.List;

public class PolicyGradient implements Policy {

    private AgentStandard agent;

    private ActorNetwork neuralNetwork;

    private int inputSize;
    private int hiddenSize;
    private List<Action> actionSet;
    private double gamma;
    private WrapperObservationVector observationWrapper;
    private WrapperActionVector actionWrapper;

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




    public void init(MLKAgent agent) {
        this.agent = (AgentStandard) agent;
        this.neuralNetwork = new ActorNetwork(inputSize, hiddenSize,pnrg(),0.001,observationWrapper, actionWrapper,actionSet);
    }

    public Action takeAction(Observation observation) {
        return neuralNetwork.selectAction(observation);
    }

    @Override
    public AgentStandard getAgent() {
        return agent;
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


