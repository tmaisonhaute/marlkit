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
import experiment.configuration.agentspec.AgentSpecInputSize;
import experiment.configuration.agentspec.AgentSpecInputWrapper;
import learning.algorithms.PPOCategorical;
import learning.policies.NeuralNetworkCategoricalPolicy;

/**
 * Provides a configurable {@link AgentModule} based on categorical PPO.
 *
 * <p>
 * By default, the resulting module uses {@link AgentStandard}, two hidden
 * layers of 32 neurons, standard PPO hyperparameters, no communication,
 * and no model of other agents.
 * </p>
 *
 * <p>
 * The provided {@link AgentSpec} must implement both
 * {@link AgentSpecInputSize} and {@link AgentSpecInputWrapper}.
 * </p>
 *
 * <p>Example using the default configuration:</p>
 *
 * <pre>{@code
 * AgentModule ppo = PPOAgentModule.builder().build();
 * }</pre>
 *
 * <p>Example using custom hyperparameters and communication:</p>
 *
 * <pre>{@code
 * CommunicationModule communication =
 *         new CommunicationModule(BroadcastRelativeObservationPositions.class);
 *
 * AgentModule ppo = PPOAgentModule.builder()
 *         .agentClass(AgentStandardCommunicating.class)
 *         .softmaxTemperature(0.8)
 *         .learningRate(0.0005)
 *         .gamma(0.99)
 *         .clipEpsilon(0.1)
 *         .ppoEpochs(8)
 *         .hiddenLayers(64, 64, 32)
 *         .communication(communication)
 *         .build();
 * }</pre>
 *
 * <p>
 * When a communication module is specified, the selected agent class must
 * implement {@link MLKAgentCommunicating}.
 * </p>
 */
public final class PPOAgentModuleBuilder {

    private static final double DEFAULT_SOFTMAX_TEMPERATURE = 1.0;

    private static final double DEFAULT_LEARNING_RATE = 0.001;
    private static final double DEFAULT_GAMMA = 0.95;
    private static final double DEFAULT_CLIP_EPSILON = 0.2;
    private static final int DEFAULT_PPO_EPOCHS = 4;

    private static final int[] DEFAULT_HIDDEN_LAYERS = { 32, 32 };

    private PPOAgentModuleBuilder() {
    }

    /**
     * Creates a builder initialized with the default PPO configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder used to configure a categorical PPO agent module.
     */
    public static final class Builder {

        private Class<? extends MLKAgent> agentClass = AgentStandard.class;

        private double softmaxTemperature = DEFAULT_SOFTMAX_TEMPERATURE;
        private double learningRate = DEFAULT_LEARNING_RATE;
        private double gamma = DEFAULT_GAMMA;
        private double clipEpsilon = DEFAULT_CLIP_EPSILON;
        private int ppoEpochs = DEFAULT_PPO_EPOCHS;

        private int[] hiddenLayers = Arrays.copyOf(DEFAULT_HIDDEN_LAYERS, DEFAULT_HIDDEN_LAYERS.length);

        private CommunicationModule communicationModule =
                new CommunicationModule(NoCommunication.class);

        private ModelOfOthersModule modelOfOthersModule =
                new ModelOfOthersModule(null);

        private Builder() {
        }

        /**
         * Sets the concrete agent class to instantiate.
         *
         * <p>
         * The class must expose a public constructor accepting a policy and
         * an algorithm.
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
         * Sets the softmax temperature used by the categorical policy.
         *
         * @param softmaxTemperature the softmax temperature
         * @return this builder
         */
        public Builder softmaxTemperature(double softmaxTemperature) {
            this.softmaxTemperature = softmaxTemperature;
            return this;
        }

        /**
         * Sets the PPO learning rate.
         *
         * @param learningRate the learning rate
         * @return this builder
         */
        public Builder learningRate(double learningRate) {
            this.learningRate = learningRate;
            return this;
        }

        /**
         * Sets the PPO discount factor.
         *
         * @param gamma the discount factor
         * @return this builder
         */
        public Builder gamma(double gamma) {
            this.gamma = gamma;
            return this;
        }

        /**
         * Sets the PPO clipping parameter.
         *
         * @param clipEpsilon the clipping parameter
         * @return this builder
         */
        public Builder clipEpsilon(double clipEpsilon) {
            this.clipEpsilon = clipEpsilon;
            return this;
        }

        /**
         * Sets the number of optimization epochs performed by PPO.
         *
         * @param ppoEpochs the number of PPO epochs
         * @return this builder
         */
        public Builder ppoEpochs(int ppoEpochs) {
            this.ppoEpochs = ppoEpochs;
            return this;
        }

        /**
         * Sets the sizes of the hidden neural-network layers.
         *
         * @param hiddenLayers the hidden layer sizes
         * @return this builder
         */
        public Builder hiddenLayers(int... hiddenLayers) {
            Objects.requireNonNull(hiddenLayers, "hiddenLayers");
            this.hiddenLayers = Arrays.copyOf(hiddenLayers, hiddenLayers.length);
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
         * Sets the model-of-others module.
         *
         * @param modelOfOthersModule the model-of-others module
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
         */
        public AgentModule build() {
            validate();

            LearningComponentsCreator learningComponentsCreator =
                    new PPOComponentsCreator(
                            softmaxTemperature,
                            learningRate,
                            gamma,
                            clipEpsilon,
                            ppoEpochs,
                            hiddenLayers
                    );

            LearningModule learningModule =
                    new LearningModule(learningComponentsCreator);

            return new AgentModule(
                    agentClass,
                    learningModule,
                    communicationModule,
                    modelOfOthersModule
            );
        }

        private void validate() {
            if (!Double.isFinite(softmaxTemperature) || softmaxTemperature <= 0.0) {
                throw new IllegalArgumentException(
                        "softmaxTemperature must be finite and greater than 0."
                );
            }

            if (!Double.isFinite(learningRate) || learningRate <= 0.0) {
                throw new IllegalArgumentException(
                        "learningRate must be finite and greater than 0."
                );
            }

            if (!Double.isFinite(gamma) || gamma < 0.0 || gamma > 1.0) {
                throw new IllegalArgumentException(
                        "gamma must be finite and between 0 and 1."
                );
            }

            if (!Double.isFinite(clipEpsilon) || clipEpsilon <= 0.0) {
                throw new IllegalArgumentException(
                        "clipEpsilon must be finite and greater than 0."
                );
            }

            if (ppoEpochs <= 0) {
                throw new IllegalArgumentException(
                        "ppoEpochs must be greater than 0."
                );
            }

            if (hiddenLayers.length == 0) {
                throw new IllegalArgumentException(
                        "At least one hidden layer must be defined."
                );
            }

            for (int hiddenLayerSize : hiddenLayers) {
                if (hiddenLayerSize <= 0) {
                    throw new IllegalArgumentException(
                            "Every hidden layer size must be greater than 0."
                    );
                }
            }
        }
    }

    /**
     * Creates fresh PPO learning components for each agent.
     */
    private static final class PPOComponentsCreator extends LearningComponentsCreator {

        private final double softmaxTemperature;
        private final double learningRate;
        private final double gamma;
        private final double clipEpsilon;
        private final int ppoEpochs;
        private final int[] hiddenLayers;

        private PPOComponentsCreator(double softmaxTemperature, double learningRate, double gamma, double clipEpsilon, int ppoEpochs, int[] hiddenLayers) {
            this.softmaxTemperature = softmaxTemperature;
            this.learningRate = learningRate;
            this.gamma = gamma;
            this.clipEpsilon = clipEpsilon;
            this.ppoEpochs = ppoEpochs;
            this.hiddenLayers = Arrays.copyOf(hiddenLayers, hiddenLayers.length);
        }

        @Override
        public LearningComponents createLearning(AgentSpec agentSpec) {
            Objects.requireNonNull(agentSpec, "agentSpec");

            if (!(agentSpec instanceof AgentSpecInputSize inputSizeSpec)) {
                throw new IllegalArgumentException(
                        "PPO requires an AgentSpec implementing AgentSpecInputSize."
                );
            }

            if (!(agentSpec instanceof AgentSpecInputWrapper inputWrapperSpec)) {
                throw new IllegalArgumentException(
                        "PPO requires an AgentSpec implementing AgentSpecInputWrapper."
                );
            }

            NeuralNetworkCategoricalPolicy policy =
                    new NeuralNetworkCategoricalPolicy(
                            agentSpec.getPossibleActions(),
                            inputWrapperSpec.getInputWrapper(),
                            inputSizeSpec.getInputSize(),
                            hiddenLayers,
                            softmaxTemperature
                    );

            PPOCategorical algorithm =
                    new PPOCategorical(
                            policy,
                            learningRate,
                            gamma,
                            clipEpsilon,
                            ppoEpochs
                    );

            return new LearningComponents(policy, algorithm);
        }
    }
}