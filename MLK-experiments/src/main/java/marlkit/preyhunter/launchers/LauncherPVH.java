package marlkit.preyhunter.launchers;

import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.preyhunter.agent.HunterAgent;
import marlkit.preyhunter.agent.PreyAgent;
import marlkit.preyhunter.environment.EnvPreyVsHunter;
import marlkit.preyhunter.scheduler.SchedulerPVH;
import marlkit.preyhunter.systemevaluator.PreyHunterSystemEvaluator;
import marlkit.preyhunter.viewer.ViewerPVH;
import reward.RewardModel;
import rewardmodelimplementation.MixedReward;
import simulation.MLKLauncher;
import simulation.MLKModel;

/**
 * Launcher for the continuous PreyHunter experiment.
 */
@EngineAgents(
        scheduler = SchedulerPVH.class,
        model = MLKModel.class,
        viewers = { ViewerPVH.class }
)
public class LauncherPVH extends MLKLauncher {

    protected static final int ENV_WIDTH = 10;
    protected static final int ENV_HEIGHT = 10;

    protected static final int NB_HUNTER_AGENTS = 2;
    protected static final int NB_PREY_AGENTS = 1;

    protected static final double CAPTURE_RADIUS = 1.2;
    protected static final double HUNTER_VIEW_RANGE = 5.0;//Double.POSITIVE_INFINITY; //5;
    protected static final double PREY_VIEW_RANGE = 0.0;//2.0;

    protected static final boolean HUNTERS_OBSERVE_OTHER_HUNTERS = true;

    protected static final int NUMBER_OF_DIRECTIONS = 4;
    protected static final double HUNTER_SPEED = 0.2;
    protected static final double PREY_SPEED = 0.15;

    protected final RewardModel rewardModel = new MixedReward();//new FullyCooperativeReward();
    
    protected static final int REQUIRED_HUNTERS_TO_CATCH = 2;

    /**
     * Create and launch the PreyHunter environment.
     *
     * @param <E> Environment type.
     * @return Created environment.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <E extends SimuEnvironment> E onLaunchEnvironment() {
        EnvPreyVsHunter env = new EnvPreyVsHunter(ENV_WIDTH, ENV_HEIGHT, CAPTURE_RADIUS, HUNTER_VIEW_RANGE, PREY_VIEW_RANGE, rewardModel, REQUIRED_HUNTERS_TO_CATCH);

        launchAgent(env, Integer.MAX_VALUE);
        
        env.setSystemEvaluator(new PreyHunterSystemEvaluator(SchedulerPVH.EPISODE_DURATION));
        return (E) env;
    }

    /**
     * Create and launch hunters and preys.
     */
    @Override
    protected void onLaunchSimulatedAgents() {
        launchHunters();
        launchPreys();
    }

    protected void launchHunters() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;

        int maxVisiblePreys = NB_PREY_AGENTS;

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgent hunter = new HunterAgent(maxVisibleHunters, maxVisiblePreys, NUMBER_OF_DIRECTIONS, HUNTER_SPEED);

            launchAgent(hunter);
        }
    }

   protected void launchPreys() {
        for (int i = 0; i < NB_PREY_AGENTS; i++) {
            PreyAgent prey = new PreyAgent(PREY_SPEED);
            launchAgent(prey);
        }
    }

    public static void main(String[] args) {
        executeThisAgent(
                "--agentLogLevel", "INFO",
                "--start"
        );
    }
}