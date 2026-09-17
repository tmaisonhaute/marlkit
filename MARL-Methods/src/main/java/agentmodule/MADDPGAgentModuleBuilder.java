package agentmodule;

import java.util.Arrays;
import java.util.Objects;

import agent.AgentStandard;
import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import communication.NoCommunication;
import experiment.configuration.AgentModule;
import experiment.configuration.CommunicationModule;
import experiment.configuration.LearningComponents;
import experiment.configuration.LearningComponentsCreator;
import experiment.configuration.LearningModule;
import experiment.configuration.ModelOfOthersModule;
import experiment.configuration.agentspec.AgentSpec;
import experiment.configuration.agentspec.AgentSpecActionSize;
import experiment.configuration.agentspec.AgentSpecCriticActionSize;
import experiment.configuration.agentspec.AgentSpecCriticInputSize;
import experiment.configuration.agentspec.AgentSpecCriticInputWrapper;
import experiment.configuration.agentspec.AgentSpecInputSize;
import experiment.configuration.agentspec.AgentSpecInputWrapper;
import experiment.configuration.agentspec.AgentSpecLowerUpperBound;
import learning.ContinuousActionExplorationStrategy;
import learning.actionexplorationstrategies.GaussianNoise;
import learning.algorithms.MADDPG;
import learning.nn.ActionValueCritic;
import learning.policies.MLPDeterministicPolicy;
import modelofotheragents.factory.MADDPGAccessOtherTargetPoliciesFactory;


/**
 * Provides a configurable {@link AgentModule} based on MADDPG.
 *
 * <p>
 * By default, the resulting module uses {@link AgentStandard}, two actor hidden
 * layers of 64 neurons, one critic hidden layer of 64 neurons, Gaussian
 * exploration noise, standard MADDPG hyperparameters, no communication, and no
 * model of other agents.
 * </p>
 *
 * <p>
 * The actor uses the local input information provided by
 * {@link AgentSpecInputSize} and {@link AgentSpecInputWrapper}. The centralized
 * critic uses the joint input information provided by
 * {@link AgentSpecCriticInputSize}, {@link AgentSpecCriticInputWrapper}, and
 * {@link AgentSpecCriticActionSize}.
 * </p>
 *
 * <p>Example using the default configuration:</p>
 *
 * <pre>{@code
 * AgentModule maddpg = MADDPGAgentModuleBuilder.builder().build();
 * }</pre>
 *
 * <p>Example using custom hyperparameters and communication:</p>
 *
 * <pre>{@code
 * CommunicationModule communication =
 *         new CommunicationModule(BroadcastRelativeObservationPositions.class);
 *
 * AgentModule maddpg = MADDPGAgentModuleBuilder.builder()
 *         .agentClass(AgentStandardCommunicating.class)
 *         .actorHiddenLayers(128, 128)
 *         .criticHiddenSize(128)
 *         .noiseCoefficient(0.2)
 *         .actorLearningRate(0.0002)
 *         .criticLearningRate(0.002)
 *         .gamma(0.98)
 *         .tau(0.01)
 *         .learningBatchSize(128)
 *         .replayBufferCapacity(200_000)
 *         .communication(communication)
 *         .build();
 * }</pre>
 *
 * <p>
 * When a communication module is specified, the selected agent class must
 * implement {@link MLKAgentCommunicating}.
 * </p>
 */
public final class MADDPGAgentModuleBuilder {

    private static final int[] DEFAULT_ACTOR_HIDDEN_LAYERS = { 64, 64 };
    private static final int DEFAULT_CRITIC_HIDDEN_SIZE = 64;

    private static final double DEFAULT_NOISE_COEFFICIENT = 0.4;

    private static final double DEFAULT_ACTOR_LEARNING_RATE = 0.0001;
    private static final double DEFAULT_CRITIC_LEARNING_RATE = 0.001;
    private static final double DEFAULT_GAMMA = 0.99;
    private static final double DEFAULT_TAU = 0.005;

    private static final int DEFAULT_LEARNING_BATCH_SIZE = 64;
    private static final int DEFAULT_REPLAY_BUFFER_CAPACITY = 100_000;

    private MADDPGAgentModuleBuilder() {
    }

    /**
     * Creates a builder initialized with the default MADDPG configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder used to configure a MADDPG agent module.
     */
    public static final class Builder {

        private Class<? extends MLKAgent> agentClass = AgentStandard.class;

        private int[] actorHiddenLayers = Arrays.copyOf(DEFAULT_ACTOR_HIDDEN_LAYERS, DEFAULT_ACTOR_HIDDEN_LAYERS.length);

        private int criticHiddenSize = DEFAULT_CRITIC_HIDDEN_SIZE;

        private double noiseCoefficient = DEFAULT_NOISE_COEFFICIENT;

        private double actorLearningRate = DEFAULT_ACTOR_LEARNING_RATE;
        private double criticLearningRate = DEFAULT_CRITIC_LEARNING_RATE;
        private double gamma = DEFAULT_GAMMA;
        private double tau = DEFAULT_TAU;

        private int learningBatchSize = DEFAULT_LEARNING_BATCH_SIZE;
        private int replayBufferCapacity = DEFAULT_REPLAY_BUFFER_CAPACITY;

        private CommunicationModule communicationModule = new CommunicationModule(NoCommunication.class);

        private ModelOfOthersModule modelOfOthersModule = new ModelOfOthersModule(new MADDPGAccessOtherTargetPoliciesFactory());

        private Builder() {
        }

        /**
         * Sets the concrete agent class to instantiate.
         *
         * <p>
         * The class must expose a public constructor accepting a policy and an
         * algorithm.
         * </p>
         *
         * @param agentClass the concrete agent class
         * @return this builder
         */
        public Builder agentClass(Class<? extends MLKAgent> agentClass) {
            this.agentClass = Objects.requireNonNull(agentClass, "agentClass");
            return this;
        }

        /**
         * Sets the sizes of the actor hidden layers.
         *
         * @param actorHiddenLayers the actor hidden layer sizes
         * @return this builder
         */
        public Builder actorHiddenLayers(int... actorHiddenLayers) {
            Objects.requireNonNull(actorHiddenLayers, "actorHiddenLayers");
            this.actorHiddenLayers = Arrays.copyOf(actorHiddenLayers, actorHiddenLayers.length);
            return this;
        }

        /**
         * Sets the size of the centralized critic hidden layer.
         *
         * @param criticHiddenSize the critic hidden layer size
         * @return this builder
         */
        public Builder criticHiddenSize(int criticHiddenSize) {
            this.criticHiddenSize = criticHiddenSize;
            return this;
        }

        /**
         * Sets the coefficient used to derive the Gaussian noise standard
         * deviation from the maximum absolute action bound.
         *
         * @param noiseCoefficient the exploration noise coefficient
         * @return this builder
         */
        public Builder noiseCoefficient(double noiseCoefficient) {
            this.noiseCoefficient = noiseCoefficient;
            return this;
        }

        /**
         * Sets the actor learning rate.
         *
         * @param actorLearningRate the actor learning rate
         * @return this builder
         */
        public Builder actorLearningRate(double actorLearningRate) {
            this.actorLearningRate = actorLearningRate;
            return this;
        }

        /**
         * Sets the centralized critic learning rate.
         *
         * @param criticLearningRate the critic learning rate
         * @return this builder
         */
        public Builder criticLearningRate(double criticLearningRate) {
            this.criticLearningRate = criticLearningRate;
            return this;
        }

        /**
         * Sets the reward discount factor.
         *
         * @param gamma the reward discount factor
         * @return this builder
         */
        public Builder gamma(double gamma) {
            this.gamma = gamma;
            return this;
        }

        /**
         * Sets the target network soft update coefficient.
         *
         * @param tau the soft update coefficient
         * @return this builder
         */
        public Builder tau(double tau) {
            this.tau = tau;
            return this;
        }

        /**
         * Sets the mini batch size used for learning.
         *
         * @param learningBatchSize the learning batch size
         * @return this builder
         */
        public Builder learningBatchSize(int learningBatchSize) {
            this.learningBatchSize = learningBatchSize;
            return this;
        }

        /**
         * Sets the replay buffer capacity.
         *
         * @param replayBufferCapacity the replay buffer capacity
         * @return this builder
         */
        public Builder replayBufferCapacity(int replayBufferCapacity) {
            this.replayBufferCapacity = replayBufferCapacity;
            return this;
        }

        /**
         * Sets the communication module.
         *
         * @param communicationModule the communication module
         * @return this builder
         */
        public Builder communication(CommunicationModule communicationModule) {
            this.communicationModule =
                    Objects.requireNonNull(communicationModule, "communicationModule");

            return this;
        }

        /**
         * Sets the model of others module.
         *
         * @param modelOfOthersModule the model of others module
         * @return this builder
         */
        public Builder modelOfOthers(ModelOfOthersModule modelOfOthersModule) {
            this.modelOfOthersModule =
                    Objects.requireNonNull(modelOfOthersModule, "modelOfOthersModule");

            return this;
        }

        /**
         * Builds the configured agent module.
         *
         * @return the resulting agent module
         * @throws IllegalArgumentException if a parameter is invalid
         */
        public AgentModule build() {
            validate();

            LearningComponentsCreator learningComponentsCreator = new MADDPGComponentsCreator(
                            actorHiddenLayers,
                            criticHiddenSize,
                            noiseCoefficient,
                            actorLearningRate,
                            criticLearningRate,
                            gamma,
                            tau,
                            learningBatchSize,
                            replayBufferCapacity
                    );

            LearningModule learningModule = new LearningModule(learningComponentsCreator);

            return new AgentModule(
                    agentClass,
                    learningModule,
                    communicationModule,
                    modelOfOthersModule
            );
        }

        private void validate() {
            if (actorHiddenLayers.length == 0) {
                throw new IllegalArgumentException(
                        "At least one actor hidden layer must be defined."
                );
            }

            for (int hiddenLayerSize : actorHiddenLayers) {
                if (hiddenLayerSize <= 0) {
                    throw new IllegalArgumentException(
                            "Every actor hidden layer size must be greater than 0."
                    );
                }
            }

            if (criticHiddenSize <= 0) {
                throw new IllegalArgumentException(
                        "criticHiddenSize must be greater than 0."
                );
            }

            if (!Double.isFinite(noiseCoefficient) || noiseCoefficient < 0.0) {
                throw new IllegalArgumentException(
                        "noiseCoefficient must be finite and greater than or equal to 0."
                );
            }

            if (!Double.isFinite(actorLearningRate) || actorLearningRate <= 0.0) {
                throw new IllegalArgumentException(
                        "actorLearningRate must be finite and greater than 0."
                );
            }

            if (!Double.isFinite(criticLearningRate) || criticLearningRate <= 0.0) {
                throw new IllegalArgumentException(
                        "criticLearningRate must be finite and greater than 0."
                );
            }

            if (!Double.isFinite(gamma) || gamma < 0.0 || gamma > 1.0) {
                throw new IllegalArgumentException(
                        "gamma must be finite and between 0 and 1."
                );
            }

            if (!Double.isFinite(tau) || tau <= 0.0 || tau > 1.0) {
                throw new IllegalArgumentException(
                        "tau must be finite, greater than 0, and less than or equal to 1."
                );
            }

            if (learningBatchSize <= 0) {
                throw new IllegalArgumentException(
                        "learningBatchSize must be greater than 0."
                );
            }

            if (replayBufferCapacity < learningBatchSize) {
                throw new IllegalArgumentException(
                        "replayBufferCapacity must be greater than or equal to learningBatchSize."
                );
            }
        }
    }

    /**
     * Creates fresh MADDPG learning components for each agent.
     */
    private static final class MADDPGComponentsCreator extends LearningComponentsCreator {

        private final int[] actorHiddenLayers;
        private final int criticHiddenSize;

        private final double noiseCoefficient;

        private final double actorLearningRate;
        private final double criticLearningRate;
        private final double gamma;
        private final double tau;

        private final int learningBatchSize;
        private final int replayBufferCapacity;

        private MADDPGComponentsCreator(int[] actorHiddenLayers, int criticHiddenSize, double noiseCoefficient, double actorLearningRate, double criticLearningRate, double gamma, double tau, int learningBatchSize, int replayBufferCapacity) {
            this.actorHiddenLayers =
                    Arrays.copyOf(actorHiddenLayers, actorHiddenLayers.length);

            this.criticHiddenSize = criticHiddenSize;
            this.noiseCoefficient = noiseCoefficient;
            this.actorLearningRate = actorLearningRate;
            this.criticLearningRate = criticLearningRate;
            this.gamma = gamma;
            this.tau = tau;
            this.learningBatchSize = learningBatchSize;
            this.replayBufferCapacity = replayBufferCapacity;
        }

        @Override
        public LearningComponents createLearning(AgentSpec agentSpec) {
            Objects.requireNonNull(agentSpec, "agentSpec");

            if (!(agentSpec instanceof AgentSpecInputSize inputSizeSpec)) {
                throw new IllegalArgumentException(
                        "MADDPG requires an AgentSpec implementing AgentSpecInputSize."
                );
            }

            if (!(agentSpec instanceof AgentSpecInputWrapper inputWrapperSpec)) {
                throw new IllegalArgumentException(
                        "MADDPG requires an AgentSpec implementing AgentSpecInputWrapper."
                );
            }

            if (!(agentSpec instanceof AgentSpecActionSize actionSizeSpec)) {
                throw new IllegalArgumentException(
                        "MADDPG requires an AgentSpec implementing AgentSpecActionSize."
                );
            }

            if (!(agentSpec instanceof AgentSpecLowerUpperBound lowerUpperBoundSpec)) {
                throw new IllegalArgumentException(
                        "MADDPG requires an AgentSpec implementing AgentSpecLowerUpperBound."
                );
            }

            if (!(agentSpec instanceof AgentSpecCriticInputSize criticInputSizeSpec)) {
                throw new IllegalArgumentException(
                        "MADDPG requires an AgentSpec implementing AgentSpecCriticInputSize."
                );
            }

            if (!(agentSpec instanceof AgentSpecCriticInputWrapper criticInputWrapperSpec)) {
                throw new IllegalArgumentException(
                        "MADDPG requires an AgentSpec implementing AgentSpecCriticInputWrapper."
                );
            }

            if (!(agentSpec instanceof AgentSpecCriticActionSize criticActionSizeSpec)) {
                throw new IllegalArgumentException(
                        "MADDPG requires an AgentSpec implementing AgentSpecCriticActionSize."
                );
            }

            double lowerBound = lowerUpperBoundSpec.getLowerBound();
            double upperBound = lowerUpperBoundSpec.getUpperBound();

            double maximumAbsoluteBound =
                    Math.max(Math.abs(lowerBound), Math.abs(upperBound));

            ContinuousActionExplorationStrategy explorationStrategy = new GaussianNoise(maximumAbsoluteBound * noiseCoefficient);

            MLPDeterministicPolicy actor = new MLPDeterministicPolicy(inputWrapperSpec.getInputWrapper(), inputSizeSpec.getInputSize(), actorHiddenLayers,
                            actionSizeSpec.getActionSize(), lowerBound, upperBound, explorationStrategy
                    );

            MLPDeterministicPolicy targetActor = new MLPDeterministicPolicy(inputWrapperSpec.getInputWrapper(), inputSizeSpec.getInputSize(), actorHiddenLayers,
                            actionSizeSpec.getActionSize(), lowerBound, upperBound
                    );

            ActionValueCritic critic = new ActionValueCritic(criticInputSizeSpec.getCriticInputSize(), criticActionSizeSpec.getCriticActionSize(),
                            criticHiddenSize, criticInputWrapperSpec.getCriticInputWrapper());

            ActionValueCritic targetCritic = new ActionValueCritic(criticInputSizeSpec.getCriticInputSize(), criticActionSizeSpec.getCriticActionSize(),
                            criticHiddenSize, criticInputWrapperSpec.getCriticInputWrapper());

            MADDPG algorithm = new MADDPG(actor, targetActor, critic, targetCritic, actorLearningRate, criticLearningRate, 
            		gamma, tau, learningBatchSize, replayBufferCapacity);

            return new LearningComponents(actor, algorithm);
        }
    }
}