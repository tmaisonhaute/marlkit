package experiment.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

import agent.MLKAgent;
import environment.MLKEnvironment;
import evaluation.SystemEvaluator;
import reward.RewardModel;
import simulation.MLKScheduler;
import trainingexecutionstrategy.DecentralizedTrainingExecutionStrategy;
import trainingexecutionstrategy.TrainingExecutionStrategy;

/**
 * Declarative description of one MARLKIT experiment configuration.
 * <p>
 * An {@code ExperimentConfiguration} defines the main components required to build
 * one experiment run: environment, reward model, scheduler, agent groups, and
 * system evaluator.
 * </p>
 * <p>
 * The reward model is configured independently from the environment. During environment
 * creation, this configuration first creates a fresh reward model through
 * {@link RewardModelModule}, then passes it to the {@link EnvironmentModule}.
 * </p>
 * <p>
 * Unlike a hand-written launcher, this class can describe several homogeneous groups
 * of agents. Each group can have its own agent class, learning module, communication
 * module, and model-of-others module.
 * </p>
 */
public class ExperimentConfiguration {

    private final String name;

    private final EnvironmentModule environmentModule;
    private final RewardModelModule rewardModelModule;
    private final SchedulerModule schedulerModule;
    private final SystemEvaluatorModule systemEvaluatorModule;

    private final List<AgentGroupConfiguration> agentGroups;
    private final AgentModule agentModule;
    
    private final OptionalInt seedIndex;

    private ExperimentConfiguration(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");

        this.environmentModule = Objects.requireNonNull(builder.environmentModule, "environmentModule");
        this.rewardModelModule = Objects.requireNonNull(builder.rewardModelModule, "rewardModelModule");
        this.schedulerModule = Objects.requireNonNull(builder.schedulerModule, "schedulerModule");
        this.systemEvaluatorModule = Objects.requireNonNull(builder.systemEvaluatorModule, "systemEvaluatorModule");

        if (builder.agentGroups.isEmpty()) {
            throw new IllegalArgumentException("At least one agent group must be defined.");
        }

        this.agentGroups = List.copyOf(builder.agentGroups);
        this.agentModule = new AgentModule();
        
        this.seedIndex = builder.seedIndex;
    }

    /**
     * Creates a builder for a named experiment configuration.
     *
     * @param name the configuration name
     * @return a new builder
     */
    public static Builder named(String name) {
        return new Builder(name);
    }

    /**
     * Returns the configuration name.
     *
     * @return the configuration name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the seed index for this configuration, if set.
     * @return an OptionalInt containing the seed index, or empty if not set
     */
	public OptionalInt getSeedIndex() {
		return seedIndex;
	}

    /**
     * Creates a fresh reward model instance.
     *
     * @return a new reward model
     */
    public RewardModel createRewardModel() {
        return rewardModelModule.createRewardModel();
    }

    /**
     * Creates a fresh environment instance using a fresh reward model.
     *
     * @return a new environment
     */
    public MLKEnvironment createEnvironment() {
        RewardModel rewardModel = createRewardModel();
        return environmentModule.createEnvironment(rewardModel);
    }

    /**
     * Returns the scheduler class used by this configuration.
     *
     * @return the scheduler class
     */
    public Class<? extends MLKScheduler> getSchedulerClass() {
        return schedulerModule.getSchedulerClass();
    }

    /**
     * Creates a fresh scheduler instance.
     *
     * @return a new scheduler
     */
    public MLKScheduler createScheduler() {
        return schedulerModule.createScheduler();
    }

    /**
     * Creates all agents for all configured agent groups.
     *
     * @return the created agents
     */
    public List<MLKAgent> createAgents() {
        List<MLKAgent> agents = new ArrayList<>();

        for (AgentGroupConfiguration groupConfiguration : agentGroups) {
            agents.addAll(agentModule.createAgents(groupConfiguration));
        }

        return agents;
    }

    /**
     * Creates a fresh system evaluator instance.
     *
     * @return a new system evaluator
     */
    public SystemEvaluator createSystemEvaluator() {
        return systemEvaluatorModule.createSystemEvaluator();
    }

    /**
     * Builder used to create an {@link ExperimentConfiguration}.
     */
    public static class Builder {

        private final String name;

        private EnvironmentModule environmentModule;
        private RewardModelModule rewardModelModule;
        private SchedulerModule schedulerModule;
        private SystemEvaluatorModule systemEvaluatorModule;
        private OptionalInt seedIndex = null;

        private final List<AgentGroupConfiguration> agentGroups = new ArrayList<>();

        private Builder(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        /**
         * Sets the environment class.
         * <p>
         * The environment class must be compatible with the default {@link EnvironmentModule},
         * usually by exposing a constructor accepting a {@link RewardModel}. If the environment
         * requires more parameters, use {@link #environment(EnvironmentModule)} instead.
         * </p>
         *
         * @param environmentClass the environment class
         * @return this builder
         */
        public Builder environment(Class<? extends MLKEnvironment> environmentClass) {
            this.environmentModule = new EnvironmentModule(environmentClass);
            return this;
        }

        /**
         * Sets a specialized environment module.
         *
         * @param environmentModule the environment module
         * @return this builder
         */
        public Builder environment(EnvironmentModule environmentModule) {
            this.environmentModule = environmentModule;
            return this;
        }

        /**
         * Sets the reward model module.
         *
         * @param rewardModelModule the reward model module
         * @return this builder
         */
        public Builder rewardModel(Class<? extends RewardModel> rewardModelClass) {
            this.rewardModelModule = new RewardModelModule(rewardModelClass);
            return this;
        }
        
        /**
         * Sets the reward model module.
         *
         * @param rewardModelModule the reward model module
         * @return this builder
         */
        public Builder rewardModel(RewardModelModule rewardModelModule) {
            this.rewardModelModule = rewardModelModule;
            return this;
        }

        /**
         * Sets the scheduler class. 
         * The training execution strategy will default to {@link DecentralizedTrainingExecutionStrategy}.
         *
         * @param schedulerClass the scheduler class
         * @return this builder
         */
        public Builder scheduler(Class<? extends MLKScheduler> schedulerClass) {
            this.schedulerModule = new SchedulerModule(schedulerClass);
            return this;
        }
        
        /**
         * Sets the scheduler class and the training execution strategy class.
         *
         * @param schedulerClass the scheduler class
         * @param trainingExecutionStrategyClass the training execution strategy class
         * @return this builder
         */
        public Builder scheduler(Class<? extends MLKScheduler> schedulerClass, Class<? extends TrainingExecutionStrategy> trainingExecutionStrategyClass) {
            this.schedulerModule = new SchedulerModule(schedulerClass, trainingExecutionStrategyClass);
            return this;
        }

        /**
         * Sets a specialized scheduler module.
         *
         * @param schedulerModule the scheduler module
         * @return this builder
         */
        public Builder scheduler(SchedulerModule schedulerModule) {
            this.schedulerModule = schedulerModule;
            return this;
        }

        /**
         * Adds an agent group to this configuration.
         *
         * @param agentGroupConfiguration the agent group configuration
         * @return this builder
         */
        public Builder agentGroup(AgentGroupConfiguration agentGroupConfiguration) {
            this.agentGroups.add(Objects.requireNonNull(agentGroupConfiguration, "agentGroupConfiguration"));
            return this;
        }

        /**
         * Sets the system evaluator module.
         *
         * @param systemEvaluatorModule the system evaluator module
         * @return this builder
         */
        public Builder systemEvaluator(SystemEvaluatorModule systemEvaluatorModule) {
            this.systemEvaluatorModule = systemEvaluatorModule;
            return this;
        }
        
        /**
         * Sets the seed index for this configuration. If set, this seed index will be used to initialize the PRNG for the experiment run.
         * If not, the PRNG will use the default seed index.
         * @param seedIndex the seed index to use for this configuration
         * @return this builder
         */
		public Builder seedIndex(int seedIndex) {
			this.seedIndex = OptionalInt.of(seedIndex);
			return this;
		}

        /**
         * Builds the experiment configuration.
         *
         * @return the built experiment configuration
         */
        public ExperimentConfiguration build() {
            return new ExperimentConfiguration(this);
        }
        
    }
}