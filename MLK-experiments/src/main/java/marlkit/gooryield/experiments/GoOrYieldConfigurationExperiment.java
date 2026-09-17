package marlkit.gooryield.experiments;

import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agentmodule.QLearningAgentModuleBuilder;
import algorithm.QValueBasedJALPolicy;
import communication.NoCommunication;
import experiment.ConfigurationRunner;
import experiment.configuration.AgentGroupConfiguration;
import experiment.configuration.AgentModule;
import experiment.configuration.CommunicationModule;
import experiment.configuration.ExperimentConfiguration;
import experiment.configuration.LearningComponents;
import experiment.configuration.LearningComponentsCreator;
import experiment.configuration.LearningModule;
import experiment.configuration.ModelOfOthersModule;
import experiment.configuration.SystemEvaluatorModule;
import experiment.configuration.agentspec.AgentSpec;
import experiment.configuration.agentspec.DefaultAgentSpec;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import madkit.kernel.Agent;
import marlkit.gooryield.SchedulerGoOrYield;
import marlkit.gooryield.agent.action.ActionGo;
import marlkit.gooryield.agent.action.ActionYield;
import marlkit.gooryield.environment.EnvGoOrYield;
import marlkit.gooryield.systemevaluator.GoOrYieldSystemEvaluator;
import modelofotheragents.factory.ActionFrequenciesModelsManagerFactory;
import modelofotheragents.factory.MinimaxModelsManagerFactory;
import rewardmodelimplementation.MixedReward;

public class GoOrYieldConfigurationExperiment extends Agent {

    private static final int SWITCH_PERIOD = 5_000;
    private static final int WINDOW_SIZE = 1_000;

    private static final String TESTED_GROUP = "tested";
    private static final String OPPONENT_GROUP = "opponent";

    @Override
    protected void onActivation() {
        super.onActivation();

        DefaultAgentSpec agentSpec = createAgentSpec();

        CommunicationModule noCommunication = new CommunicationModule(NoCommunication.class);

        ModelOfOthersModule noModelOfOthers = new ModelOfOthersModule(null);

        LearningModule scriptedLearning = new LearningModule(new ScriptedLearningComponentsCreator(SWITCH_PERIOD));
        
        LearningModule jalLearning = new LearningModule(new JALLearningComponentsCreator());
        
        SystemEvaluatorModule gooryieldEvaluator = new SystemEvaluatorModule(GoOrYieldSystemEvaluator.class);

        AgentModule independentModule = QLearningAgentModuleBuilder.builder()
                        .agentClass(AgentStandard.class)
                        .initialEpsilon(1.0)
                        .epsilonDecay(0.0001)
                        .alpha(0.2)
                        .gamma(0.95)
                        .build();

        AgentModule scriptedModule = new AgentModule(
                        AgentStandard.class,
                        scriptedLearning,
                        noCommunication,
                        noModelOfOthers
                );

        AgentModule frequencyModule = new AgentModule(
        		AgentStandardPredictingOthersAction.class,
                        jalLearning,
                        noCommunication,
                        new ModelOfOthersModule(new ActionFrequenciesModelsManagerFactory(new ActionYield(),WINDOW_SIZE)).targetGroup(OPPONENT_GROUP)
                );

        AgentModule minimaxModule = new AgentModule(
        		AgentStandardPredictingOthersAction.class,
                        jalLearning,
                        noCommunication,
                        new ModelOfOthersModule(new MinimaxModelsManagerFactory()).targetGroup(OPPONENT_GROUP)
                );

        ExperimentConfiguration independent = ExperimentConfiguration.named("IndependentVsScripted")
        		.environment(EnvGoOrYield.class)
        		.rewardModel(MixedReward.class)
        		.scheduler(SchedulerGoOrYield.class)
        		.agentGroup(new AgentGroupConfiguration(1, independentModule, agentSpec))
        		.agentGroup(new AgentGroupConfiguration(1, scriptedModule, agentSpec))
        		.systemEvaluator(gooryieldEvaluator)
        		.build();
        
        ExperimentConfiguration frequency = ExperimentConfiguration.named("FrequencyVsScripted")
        		.environment(EnvGoOrYield.class)
        		.rewardModel(MixedReward.class)
        		.scheduler(SchedulerGoOrYield.class)
        		.agentGroup(new AgentGroupConfiguration(1, frequencyModule, agentSpec, TESTED_GROUP))
        		.agentGroup(new AgentGroupConfiguration(1, scriptedModule, agentSpec, OPPONENT_GROUP))
        		.systemEvaluator(gooryieldEvaluator)
        		.seedIndex(4)
        		.build();
        
        ExperimentConfiguration minimax = ExperimentConfiguration.named("MinimaxVsScripted")
        		.environment(EnvGoOrYield.class)
        		.rewardModel(MixedReward.class)
        		.scheduler(SchedulerGoOrYield.class)
        		.agentGroup(new AgentGroupConfiguration(1, minimaxModule, agentSpec, TESTED_GROUP))
        		.agentGroup(new AgentGroupConfiguration(1, scriptedModule, agentSpec, OPPONENT_GROUP))
        		.systemEvaluator(gooryieldEvaluator)
        		.build();


        launchAgent(new ConfigurationRunner(List.of(frequency), 1));

    }

    public static void main(String[] args) {
    	executeThisAgent("--agentLogLevel", "INFO",
                "--start");
    }
    
    private static DefaultAgentSpec createAgentSpec() {
        List<Action> possibleActions = List.of(new ActionYield(), new ActionGo());

        return new DefaultAgentSpec(possibleActions);
    }
    
    private static class JALLearningComponentsCreator extends LearningComponentsCreator {

        private static final double INITIAL_EPSILON = 1.0;
        private static final double EPSILON_DECAY = 0.0001;
        private static final double ALPHA = 0.2;
        private static final double GAMMA = 0.95;

        @Override
        public LearningComponents createLearning(AgentSpec agentSpec) {

            QValueBasedJALPolicy policy = new QValueBasedJALPolicy(agentSpec.getPossibleActions());

            policy.setExplorationStrategy(new EpsilonGreedyExponentialDecay(INITIAL_EPSILON, EPSILON_DECAY));

            QLearning algorithm = new QLearning(policy, agentSpec.getPossibleActions(), ALPHA, GAMMA);

            return new LearningComponents(policy, algorithm);
        }
    }
    

}
                		