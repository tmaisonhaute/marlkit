package agentModule;

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
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;

/**
 * Provides a configurable {@link AgentModule} based on Q-learning.
 *
 * <p>
 * By default, the resulting module uses {@link AgentStandard}, standard
 * Q-learning hyperparameters, no communication, and no model of other agents.
 * </p>
 *
 * <p>Example using the default configuration:</p>
 *
 * <pre>{@code
 * AgentModule qLearning = QLearningAgentModule.builder().build();
 * }</pre>
 *
 * <p>Example using custom hyperparameters and communication:</p>
 *
 * <pre>{@code
 * CommunicationModule communication =
 *         new CommunicationModule(BroadcastRelativeObservationPositions.class);
 *
 * AgentModule qLearning = QLearningAgentModule.builder()
 *         .agentClass(AgentStandardCommunicating.class)
 *         .initialEpsilon(0.8)
 *         .epsilonDecay(0.002)
 *         .alpha(0.1)
 *         .gamma(0.99)
 *         .communication(broadcastObservationModule)
 *         .build();
 * }</pre>
 *
 * <p>
 * When a communication module is specified, the selected agent class must
 * implement {@link MLKAgentCommunicating}.
 * </p>
 */
public final class QLearningAgentModule {

    private static final double DEFAULT_INITIAL_EPSILON = 1.0;
    private static final double DEFAULT_EPSILON_DECAY = 0.001;
    private static final double DEFAULT_ALPHA = 0.2;
    private static final double DEFAULT_GAMMA = 0.95;

    private QLearningAgentModule() {
    }

    /**
     * Creates a builder initialized with the default Q-learning configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder used to configure a Q-learning agent module.
     */
    public static final class Builder {

        private Class<? extends MLKAgent> agentClass = AgentStandard.class;

        private double initialEpsilon = DEFAULT_INITIAL_EPSILON;
        private double epsilonDecay = DEFAULT_EPSILON_DECAY;
        private double alpha = DEFAULT_ALPHA;
        private double gamma = DEFAULT_GAMMA;

        private CommunicationModule communicationModule = new CommunicationModule(NoCommunication.class);
        private ModelOfOthersModule modelOfOthersModule = new ModelOfOthersModule(null);

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
         * Sets the initial epsilon value used by the exploration strategy.
         *
         * @param initialEpsilon the initial epsilon value
         * @return this builder
         */
        public Builder initialEpsilon(double initialEpsilon) {
            this.initialEpsilon = initialEpsilon;
            return this;
        }

        /**
         * Sets the epsilon decay rate.
         *
         * @param epsilonDecay the epsilon decay rate
         * @return this builder
         */
        public Builder epsilonDecay(double epsilonDecay) {
            this.epsilonDecay = epsilonDecay;
            return this;
        }

        /**
         * Sets the Q-learning learning rate.
         *
         * @param alpha the learning rate
         * @return this builder
         */
        public Builder alpha(double alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * Sets the Q-learning discount factor.
         *
         * @param gamma the discount factor
         * @return this builder
         */
        public Builder gamma(double gamma) {
            this.gamma = gamma;
            return this;
        }

        /**
         * Sets the communication module.
         *
         * @param communicationModule the communication module
         * @return this builder
         */
        public Builder communication(CommunicationModule communicationModule) {
            this.communicationModule = Objects.requireNonNull(communicationModule, "communicationModule");
            return this;
        }

        /**
         * Sets the model of others module.
         *
         * @param modelOfOthersModule the model-of-others module
         * @return this builder
         */
        public Builder modelOfOthers(ModelOfOthersModule modelOfOthersModule) {
            this.modelOfOthersModule = Objects.requireNonNull(modelOfOthersModule, "modelOfOthersModule");
            return this;
        }

        /**
         * Builds the configured agent module.
         *
         * @return the resulting agent module
         * @throws IllegalArgumentException if any parameter is invalid
         */
        public AgentModule build() {
            validate();

            LearningComponentsCreator learningComponentsCreator =
                    new QLearningComponentsCreator(initialEpsilon, epsilonDecay, alpha, gamma);

            LearningModule learningModule =
                    new LearningModule(learningComponentsCreator);

            return new AgentModule(agentClass, learningModule, communicationModule, modelOfOthersModule);
        }

        private void validate() {
            if (initialEpsilon < 0.0 || initialEpsilon > 1.0) {
                throw new IllegalArgumentException("initialEpsilon must be between 0 and 1.");
            }

            if (epsilonDecay < 0.0) {
                throw new IllegalArgumentException("epsilonDecay must be >= 0.");
            }

            if (alpha <= 0.0 || alpha > 1.0) {
                throw new IllegalArgumentException("alpha must be greater than 0 and less than or equal to 1.");
            }

            if (gamma < 0.0 || gamma > 1.0) {
                throw new IllegalArgumentException("gamma must be between 0 and 1.");
            }
        }
    }

    /**
     * Creates fresh Q-learning components for each agent.
     */
    private static final class QLearningComponentsCreator extends LearningComponentsCreator {

        private final double initialEpsilon;
        private final double epsilonDecay;
        private final double alpha;
        private final double gamma;

        private QLearningComponentsCreator(double initialEpsilon, double epsilonDecay, double alpha, double gamma) {
            this.initialEpsilon = initialEpsilon;
            this.epsilonDecay = epsilonDecay;
            this.alpha = alpha;
            this.gamma = gamma;
        }

        @Override
        public LearningComponents createLearning(AgentSpec agentSpec) {
            Objects.requireNonNull(agentSpec, "agentSpec");

            QValueBasedPolicy policy = new QValueBasedPolicy(
                    agentSpec.getPossibleActions(),
                    initialEpsilon,
                    new EpsilonGreedyExponentialDecay(initialEpsilon, epsilonDecay)
            );

            QLearning algorithm =
                    new QLearning(policy, agentSpec.getPossibleActions(), alpha, gamma);

            return new LearningComponents(policy, algorithm);
        }
    }
}