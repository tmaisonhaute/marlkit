package learning.policy;

import java.util.List;
import java.util.Random;

import agent.action.Action;
import agent.action.wrapperactionvector.WrapperAction2DMoveVector;
import agent.action.wrapperactionvector.WrapperActionVector;
import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;
import environment.observation.wrapperobservationvector.WrapperObservationVectorPositionsValues;
import learning.Batch;
import learning.Experience;
import learning.nn.ActorNetwork;
import learning.nn.CriticNetwork;
import madkit.kernel.AgentLogger;

public class PolicyActorCritic extends PolicyEpsilon {

    // Neural network based actor and critic
    private CriticNetwork critic;
    private ActorNetwork actor;
    
    // Hyperparameters
    private List<Action> actionsSet;
    private double gamma;       // Discount factor
    private Random random;
    private double totalRewards;
    
    // Wrappers for converting observations and actions to vectors
    private WrapperObservationVector observationWrapper;
    private WrapperActionVector actionWrapper;
    
    /**
     * Constructor with wrappers for neural network based actor-critic.
     */
    public PolicyActorCritic(List<Action> actionsSet, 
                           int inputSize,
                           double epsilon, 
                           double epsilonDecrease) {
        super(epsilon, epsilonDecrease);
        this.actionsSet = actionsSet;
        this.observationWrapper = new WrapperObservationVectorPositionsValues(false);
        this.actionWrapper = new WrapperAction2DMoveVector();
        this.gamma = 0.95;
        this.random = new Random();
        this.totalRewards = 0.0;
        
        // Initialize neural networks with default hidden size
        int hiddenSize = 64; // Can be adjusted based on the problem complexity
        this.critic = new CriticNetwork(inputSize, hiddenSize, 0.001, this.observationWrapper);
        this.actor = new ActorNetwork(inputSize, hiddenSize, 0.001, observationWrapper, actionWrapper, actionsSet);
    }

    public PolicyActorCritic(List<Action> actionsSet, int inputSize,
                           double epsilon) {
        this(actionsSet, inputSize, epsilon, 0.0);
    }

    public PolicyActorCritic(List<Action> actionsSet, int inputSize) {
        this(actionsSet, inputSize, 0.05);
    }

    @Override
    public int getLearningFrequency() {
        return 1;
    }

    @Override
    public Action takeAction(Observation observation) {
        return actor.selectAction(observation, getEpsilon());
    }

    protected double updateCritic(Observation state, double reward, Observation nextState) {
        // Calculate TD target
        double tdTarget;
        if (nextState != null) {
            double vNext = critic.getValue(nextState);
            tdTarget = reward + gamma * vNext;
        } else {
            tdTarget = reward; // Terminal state
        }
        
        // Update critic and get TD error
        return critic.update(state, tdTarget);
    }
    
    protected void updateActor(Observation state, Action action, double tdError) {
        // Update actor using TD error as advantage
        actor.update(state, action, tdError);
    }
    
    protected void update(Observation state, Action action, double reward, Observation nextState) {
        // Update critic and get TD error
        double tdError = updateCritic(state, reward, nextState);
        
        // Update actor using TD error
        updateActor(state, action, tdError);
    }

    @Override
    public void learnOnBatch(Batch batch, AgentLogger logger) {
        while (batch.getExperiences().size() >= 1) {
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
        
        update(currentState, currentAction, reward, nextState);
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
            
            update(lastState, lastAction, lastReward, null);
            batch.getExperiences().remove(0);
            totalRewards += lastReward;
        }
        
        updateEpsilon();
        logger.info("total rewards : " + totalRewards);
        logger.info("Epsilon : " + getEpsilon());
        totalRewards = 0.0;
    }
    
    // Getters for the neural networks
    public CriticNetwork getCritic() {
        return critic;
    }
    
    public ActorNetwork getActor() {
        return actor;
    }
}
