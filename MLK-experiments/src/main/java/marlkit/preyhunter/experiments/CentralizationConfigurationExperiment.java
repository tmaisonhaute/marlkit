package marlkit.preyhunter.experiments;

import java.util.List;

import agent.action.Action;
import agent.action.Move2DDouble;
import agentmodule.DDPGAgentModuleBuilder;
import agentmodule.MADDPGAgentModuleBuilder;
import communication.NoCommunication;
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
import experiment.configuration.agentspec.AgentSpecInputSize;
import experiment.configuration.agentspec.AgentSpecInputWrapper;
import experiment.configuration.agentspec.AgentSpecLowerUpperBound;
import experiment.configuration.agentspec.DefaultAgentSpec;
import madkit.kernel.Agent;
import marlkit.preyhunter.agent.HunterAgent;
import marlkit.preyhunter.agent.HunterAgentDDPG;
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

        LearningModule preyLearning =
                new LearningModule(new PreyComponentsCreator(PREY_SPEED));

        CommunicationModule noCommunication =
                new CommunicationModule(NoCommunication.class);

        ModelOfOthersModule noModelOfOthers =
                new ModelOfOthersModule(null);

        SystemEvaluatorModule preyHunterSystemEvaluator =
                new SystemEvaluatorModule(PreyHunterSystemEvaluator.class);

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
				.agentClass(HunterAgent.class)
				.build();
        
        

        ExperimentConfiguration ddpg = ExperimentConfiguration.named("DDPG")
                .environment(environmentModule)
                .rewardModel(MixedReward.class)
                .experienceBuilder(new TransitionExperienceBuilder())
                .scheduler(SchedulerPVHNoPause.class)
                .agentGroup(new AgentGroupConfiguration(
                        NB_HUNTER_AGENTS,
                        ddpgAgentModule,
                        createHunterSpec()
                ))
                .agentGroup(new AgentGroupConfiguration(
                        NB_PREY_AGENTS,
                        preyAgentModule,
                        createPreySpec()
                ))
                .systemEvaluator(preyHunterSystemEvaluator)
                .build();
        
//        ExperimentConfiguration maddpg = ExperimentConfiguration.named("PVH_PPO_NoCommunication")
//                .environment(environmentModule)
//                .rewardModel(MixedReward.class)
//                .scheduler(SchedulerPVHCentralizedCriticNoPause.class)
//                .agentGroup(new AgentGroupConfiguration(
//                        NB_HUNTER_AGENTS,
//                        maddpgAgentModule,
//                        createHunterSpec()
//                ))
//                .agentGroup(new AgentGroupConfiguration(
//                        NB_PREY_AGENTS,
//                        preyAgentModule,
//                        createPreySpec()
//                ))
//                .systemEvaluator(preyHunterSystemEvaluator)
//                .build();
        


        launchAgent(new ConfigurationRunner(List.of(ddpg)));

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

    private static class HunterAgentSpec implements AgentSpecInputSize, AgentSpecInputWrapper, AgentSpecActionSize,
		    AgentSpecCriticInputSize, AgentSpecCriticActionSize, AgentSpecLowerUpperBound {
		
		private static final int ACTION_SIZE = 2;
		
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
}