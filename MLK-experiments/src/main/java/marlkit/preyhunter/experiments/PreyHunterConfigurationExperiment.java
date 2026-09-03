package marlkit.preyhunter.experiments;

import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2DDouble;
import agent.communication.AgentStandardCommunicating;
import communication.NoCommunication;
import communicationimplementation.BroadcastRelativeObservationPositions;
import environment.observation.wrapperobservationvector.WrapperObservationVector;
import evaluation.NoSystemEvaluation;
import experiment.ConfigurationRunner;
import experiment.configuration.AgentGroupConfiguration;
import experiment.configuration.CommunicationModule;
import experiment.configuration.ExperimentConfiguration;
import experiment.configuration.LearningModule;
import experiment.configuration.ModelOfOthersModule;
import experiment.configuration.SystemEvaluatorModule;
import experiment.configuration.agentspec.AgentSpecInputSize;
import experiment.configuration.agentspec.AgentSpecInputWrapper;
import experiment.configuration.agentspec.DefaultAgentSpec;
import madkit.kernel.Agent;
import marlkit.preyhunter.agent.PreyAgent;
import marlkit.preyhunter.environment.EnvPreyVsHunter;
import marlkit.preyhunter.environment.WrapperPreyHunterObservationVector;
import marlkit.preyhunter.scheduler.SchedulerPVH;
import rewardmodelimplementation.MixedReward;

public class PreyHunterConfigurationExperiment extends Agent{

    private static final int NB_HUNTER_AGENTS = 2;
    private static final int NB_PREY_AGENTS = 1;

    private static final boolean HUNTERS_OBSERVE_OTHER_HUNTERS = true;

    private static final int NUMBER_OF_DIRECTIONS = 4;
    private static final double HUNTER_SPEED = 0.2;
    private static final double PREY_SPEED = 0.15;
    
    @Override
    protected void onActivation() {
    	super.onActivation();
    	LearningModule hunterPPOLearning =
                new LearningModule(new HunterPPOLearningComponentsCreator());

        LearningModule preyLearning =
                new LearningModule(new PreyComponentsCreator(PREY_SPEED));

        CommunicationModule noCommunication =
                new CommunicationModule(NoCommunication.class);

        CommunicationModule broadcastObservation =
                new CommunicationModule(BroadcastRelativeObservationPositions.class);

        ModelOfOthersModule noModelOfOthers =
                new ModelOfOthersModule(null);

        SystemEvaluatorModule noSystemEvaluator =
                new SystemEvaluatorModule(NoSystemEvaluation.class);

        ExperimentConfiguration ppoNoCommunication = ExperimentConfiguration.named("PVH_PPO_NoCommunication")
                .environment(EnvPreyVsHunter.class)
                .rewardModel(MixedReward.class)
                .scheduler(SchedulerPVH.class)
                .agentGroup(new AgentGroupConfiguration(
                        AgentStandard.class,
                        NB_HUNTER_AGENTS,
                        createHunterSpec(),
                        hunterPPOLearning,
                        noCommunication,
                        noModelOfOthers
                ))
                .agentGroup(new AgentGroupConfiguration(
                        PreyAgent.class,
                        NB_PREY_AGENTS,
                        createPreySpec(),
                        preyLearning,
                        noCommunication,
                        noModelOfOthers
                ))
                .systemEvaluator(noSystemEvaluator)
                .build();

        ExperimentConfiguration ppoBroadcastObservation = ExperimentConfiguration.named("PVH_PPO_BroadcastObservation")
                .environment(EnvPreyVsHunter.class)
                .rewardModel(MixedReward.class)
                .scheduler(SchedulerPVH.class)
                .agentGroup(new AgentGroupConfiguration(
                        AgentStandardCommunicating.class,
                        NB_HUNTER_AGENTS,
                        createHunterSpec(),
                        hunterPPOLearning,
                        broadcastObservation,
                        noModelOfOthers
                ))
                .agentGroup(new AgentGroupConfiguration(
                        PreyAgent.class,
                        NB_PREY_AGENTS,
                        createPreySpec(),
                        preyLearning,
                        noCommunication,
                        noModelOfOthers
                ))
                .systemEvaluator(noSystemEvaluator)
                .build();

        launchAgent(new ConfigurationRunner(List.of(ppoNoCommunication, ppoBroadcastObservation)));

    }

    public static void main(String[] args) {
    	executeThisAgent("--agentLogLevel", "INFO",
                "--start");
    }

    private static HunterAgentSpec createHunterSpec() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;

        List<Action> actions = Move2DDouble.getDirectionalMoves(NUMBER_OF_DIRECTIONS, HUNTER_SPEED);
        WrapperPreyHunterObservationVector wrapper =
                new WrapperPreyHunterObservationVector(maxVisibleHunters, maxVisiblePreys);

        return new HunterAgentSpec(actions, wrapper);
    }

    private static DefaultAgentSpec createPreySpec() {
        List<Action> actions = Move2DDouble.getDirectionalMoves(NUMBER_OF_DIRECTIONS, PREY_SPEED);
        return new DefaultAgentSpec(actions);
    }

    private static class HunterAgentSpec implements AgentSpecInputSize, AgentSpecInputWrapper {

        private final List<Action> possibleActions;
        private final WrapperObservationVector inputWrapper;
        private final int inputSize;

        private HunterAgentSpec(List<Action> possibleActions, WrapperPreyHunterObservationVector inputWrapper) {
            this.possibleActions = List.copyOf(possibleActions);
            this.inputWrapper = inputWrapper;
            this.inputSize = inputWrapper.getVectorSize();
        }

        @Override
        public List<Action> getPossibleActions() {
            return possibleActions;
        }

        @Override
        public int getInputSize() {
            return inputSize;
        }

        @Override
        public WrapperObservationVector getInputWrapper() {
            return inputWrapper;
        }

		@Override
		public List<String> getOtherRoleNames() {
			return List.of("Hunter");
		}
    }
}