package marlkit.preyhunter.experiments;

import java.util.List;

import agent.action.Action;
import agent.action.Move2DDouble;
import agentmodule.DDPGAgentModuleBuilder;
import agentmodule.MADDPGAgentModuleBuilder;
import centralizedtraining.CentralizedCriticTrainingExecutionStrategy;
import communication.NoCommunication;
import environment.observation.wrapperobservationvector.WrapperJointObservation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;
import experience.TransitionExperienceBuilder;
import experiment.ConfigurationRunner;
import experiment.configuration.AgentGroupConfiguration;
import experiment.configuration.AgentModule;
import experiment.configuration.CommunicationModule;
import experiment.configuration.ConfigurableEnvironmentModule;
import experiment.configuration.EnvironmentModule;
import experiment.configuration.ExperimentConfiguration;
import experiment.configuration.LearningModule;
import experiment.configuration.ModelOfOthersModule;
import experiment.configuration.SystemEvaluatorModule;
import experiment.configuration.agentspec.AgentSpecActionSize;
import experiment.configuration.agentspec.AgentSpecCriticActionSize;
import experiment.configuration.agentspec.AgentSpecCriticInputSize;
import experiment.configuration.agentspec.AgentSpecCriticInputWrapper;
import experiment.configuration.agentspec.AgentSpecInputSize;
import experiment.configuration.agentspec.AgentSpecInputWrapper;
import experiment.configuration.agentspec.AgentSpecLowerUpperBound;
import experiment.configuration.agentspec.DefaultAgentSpec;
import madkit.kernel.Agent;
import marlkit.preyhunter.agent.HunterAgentDDPG;
import marlkit.preyhunter.agent.HunterAgentMADDPG;
import marlkit.preyhunter.agent.PreyAgent;
import marlkit.preyhunter.environment.EnvPreyVsHunter;
import marlkit.preyhunter.environment.WrapperPreyHunterObservationVector;
import marlkit.preyhunter.launchers.LauncherPVH;
import marlkit.preyhunter.scheduler.SchedulerPVHNoPause;
import marlkit.preyhunter.systemevaluator.PreyHunterSystemEvaluator;
import rewardmodelimplementation.MixedReward;

public class CentralizationConfigurationExperiment extends Agent{

    private static final int NB_HUNTER_AGENTS = 2;
    private static final int NB_PREY_AGENTS = 1;

    private static final boolean HUNTERS_OBSERVE_OTHER_HUNTERS = true;

    private static final int NUMBER_OF_DIRECTIONS = 4;
    private static final double HUNTER_SPEED = 0.2;
    private static final double PREY_SPEED = 0.15;
    
    @Override
    protected void onActivation() {
    	super.onActivation();
    	
    	EnvironmentModule environmentModule = new ConfigurableEnvironmentModule(EnvPreyVsHunter.class, LauncherPVH.ENV_WIDTH, LauncherPVH.ENV_HEIGHT, 
    			LauncherPVH.CAPTURE_RADIUS, LauncherPVH.HUNTER_VIEW_RANGE, LauncherPVH.PREY_VIEW_RANGE, LauncherPVH.REQUIRED_HUNTERS_TO_CATCH);

        LearningModule preyLearning = new LearningModule(new PreyComponentsCreator(PREY_SPEED));

        CommunicationModule noCommunication = new CommunicationModule(NoCommunication.class);

        ModelOfOthersModule noModelOfOthers = new ModelOfOthersModule(null);

        SystemEvaluatorModule preyHunterSystemEvaluator = new SystemEvaluatorModule(PreyHunterSystemEvaluator.class);

        AgentModule preyAgentModule = new AgentModule(
        		PreyAgent.class,
        		preyLearning,
        		noCommunication,
        		noModelOfOthers
        		);

        
        AgentModule ddpgAgentModule = DDPGAgentModuleBuilder.builder()
        		.agentClass(HunterAgentDDPG.class)
        		.build();
        
		AgentModule maddpgAgentModule = MADDPGAgentModuleBuilder.builder()
				.agentClass(HunterAgentMADDPG.class)
				.build();
        
        

        ExperimentConfiguration ddpg = ExperimentConfiguration.named("DDPG")
                .environment(environmentModule)
                .rewardModel(MixedReward.class)
                .experienceBuilder(new TransitionExperienceBuilder())
                .scheduler(SchedulerPVHNoPause.class)
                .agentGroup(new AgentGroupConfiguration(
                        NB_HUNTER_AGENTS,
                        ddpgAgentModule,
                        createDDPGHunterSpec()
                ))
                .agentGroup(new AgentGroupConfiguration(
                        NB_PREY_AGENTS,
                        preyAgentModule,
                        createPreySpec()
                ))
                .systemEvaluator(preyHunterSystemEvaluator)
                .build();
        
        ExperimentConfiguration maddpg = ExperimentConfiguration.named("MADDPG")
                .environment(environmentModule)
                .rewardModel(MixedReward.class)
                .experienceBuilder(new TransitionExperienceBuilder())
                .scheduler(SchedulerPVHNoPause.class, CentralizedCriticTrainingExecutionStrategy.class)
                .agentGroup(new AgentGroupConfiguration(
                        NB_HUNTER_AGENTS,
                        maddpgAgentModule,
                        createMADDPGHunterSpec()
                ))
                .agentGroup(new AgentGroupConfiguration(
                        NB_PREY_AGENTS,
                        preyAgentModule,
                        createPreySpec()
                ))
                .systemEvaluator(preyHunterSystemEvaluator)
                .seedIndex(0)
                .build();
        


        launchAgent(new ConfigurationRunner(List.of(maddpg), 1));

    }

    public static void main(String[] args) {
    	executeThisAgent("--agentLogLevel", "INFO",
                "--start");
    }

    private static DDPGHunterAgentSpec createDDPGHunterSpec() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;

        List<Action> actions = Move2DDouble.getDirectionalMoves(NUMBER_OF_DIRECTIONS, HUNTER_SPEED);
        WrapperPreyHunterObservationVector wrapper = new WrapperPreyHunterObservationVector(maxVisibleHunters, maxVisiblePreys);

        return new DDPGHunterAgentSpec(actions, wrapper);
    }
    
    private static MADDPGHunterAgentSpec createMADDPGHunterSpec() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;

        List<Action> actions = Move2DDouble.getDirectionalMoves(NUMBER_OF_DIRECTIONS, HUNTER_SPEED);
        WrapperPreyHunterObservationVector actorWrapper = new WrapperPreyHunterObservationVector(maxVisibleHunters, maxVisiblePreys);

        return new MADDPGHunterAgentSpec(actions, actorWrapper, NB_HUNTER_AGENTS);
    }

    private static DefaultAgentSpec createPreySpec() {
        List<Action> actions = Move2DDouble.getDirectionalMoves(NUMBER_OF_DIRECTIONS, PREY_SPEED);
        return new DefaultAgentSpec(actions);
    }

    private static class DDPGHunterAgentSpec implements AgentSpecInputSize, AgentSpecInputWrapper, AgentSpecActionSize,
		    AgentSpecCriticInputSize, AgentSpecCriticActionSize, AgentSpecLowerUpperBound {
		
		private static final int ACTION_SIZE = 2;
		
		private final List<Action> possibleActions;
		private final WrapperObservationVector inputWrapper;
		private final int inputSize;
		
		protected DDPGHunterAgentSpec(List<Action> possibleActions, WrapperPreyHunterObservationVector inputWrapper) {
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
		
		@Override
		public int getActionSize() {
		    return ACTION_SIZE;
		}
		
		@Override
		public double getLowerBound() {
		    return -HUNTER_SPEED;
		}
		
		@Override
		public double getUpperBound() {
		    return HUNTER_SPEED;
		}
		
		@Override
		public int getCriticActionSize() {
		    return ACTION_SIZE * NB_HUNTER_AGENTS;
		}
		
		@Override
		public int getCriticInputSize() {
		    return inputSize * NB_HUNTER_AGENTS;
		}
    }
    
    private static class MADDPGHunterAgentSpec extends DDPGHunterAgentSpec implements AgentSpecCriticInputWrapper {

        private final WrapperObservationVector criticInputWrapper;
        private final int criticInputSize;
        private final int criticActionSize;

        private MADDPGHunterAgentSpec(List<Action> possibleActions, WrapperPreyHunterObservationVector actorInputWrapper, int numberOfHunters) {
            super(possibleActions, actorInputWrapper);

            WrapperJointObservation jointWrapper = new WrapperJointObservation(actorInputWrapper, actorInputWrapper.getVectorSize(), numberOfHunters);

            this.criticInputWrapper = jointWrapper;
            this.criticInputSize = jointWrapper.getVectorSize();
            this.criticActionSize = getActionSize() * numberOfHunters;
        }

        @Override
        public WrapperObservationVector getCriticInputWrapper() {
            return criticInputWrapper;
        }

        @Override
        public int getCriticInputSize() {
            return criticInputSize;
        }

        @Override
        public int getCriticActionSize() {
            return criticActionSize;
        }
    }
}