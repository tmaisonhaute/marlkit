package marlkit.pushtheblocktogether.experiments;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2DInt;
import communication.NoCommunication;
import evaluation.NoSystemEvaluation;
import experiment.ConfigurationRunner;
import experiment.configuration.AgentGroupConfiguration;
import experiment.configuration.CommunicationModule;
import experiment.configuration.ExperimentConfiguration;
import experiment.configuration.LearningModule;
import experiment.configuration.ModelOfOthersModule;
import experiment.configuration.RewardModelModule;
import experiment.configuration.SystemEvaluatorModule;
import experiment.configuration.agentspec.DefaultAgentSpec;
import madkit.kernel.Agent;
import marlkit.pushtheblock.SchedulerPTBNoPause;
import marlkit.pushtheblocktogether.EnvPushTheBlockTogether;
import rewardmodelimplementation.MixedReward;

public class PushTheBlockTogetherConfigurationExperiment extends Agent {

    private static final int NB_AGENTS = 2;
    
    @Override
    protected void onActivation() {
    	super.onActivation();
    	LearningModule qLearningModule =
                new LearningModule(new PushTheBlockQLearningComponentsCreator());

        CommunicationModule noCommunication =
                new CommunicationModule(NoCommunication.class);

        ModelOfOthersModule noModelOfOthers =
                new ModelOfOthersModule(null);

        SystemEvaluatorModule noSystemEvaluator =
                new SystemEvaluatorModule(NoSystemEvaluation.class);

        ExperimentConfiguration qLearningConfig = ExperimentConfiguration.named("PTBTogether_QLearning")
                .environment(EnvPushTheBlockTogether.class)
                .rewardModel(new RewardModelModule(MixedReward.class))
                .scheduler(SchedulerPTBNoPause.class)
                .agentGroup(new AgentGroupConfiguration(
                        AgentStandard.class,
                        NB_AGENTS,
                        createAgentSpec(),
                        qLearningModule,
                        noCommunication,
                        noModelOfOthers
                ))
                .systemEvaluator(noSystemEvaluator)
                .build();

        launchAgent(new ConfigurationRunner(List.of(qLearningConfig)));

    }
    

    public static void main(String[] args) {
    	executeThisAgent("--agentLogLevel", "INFO",
                "--start");
    }

    private static DefaultAgentSpec createAgentSpec() {
        List<Action> possibleActions = new ArrayList<>(List.of(
                Move2DInt.left(),
                Move2DInt.right(),
                Move2DInt.up(),
                Move2DInt.down()
        ));

        return new DefaultAgentSpec(possibleActions);
    }
}