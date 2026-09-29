package marlkit.preyhunter.launchers;

import java.util.ArrayList;
import java.util.List;

import experience.TransitionExperienceBuilder;
import madkit.simulation.EngineAgents;
import marlkit.preyhunter.agent.HunterAgentMADDPG;
import marlkit.preyhunter.environment.EnvPreyVsHunter;
import marlkit.preyhunter.scheduler.SchedulerPVHCentralizedCritic;
import marlkit.preyhunter.viewer.ViewerPVH;
import simulation.MLKModel;
import simulation.LauncherMetadata;

@EngineAgents(
        scheduler = SchedulerPVHCentralizedCritic.class,
        model = MLKModel.class,
        viewers = { ViewerPVH.class }
)
@LauncherMetadata(
    title = "MADDPG Centralized Critic",
    documentationAnchor = "maddpg-centralized-critic")
public class LauncherPVHMADDPG extends LauncherPVH {

	@Override
	protected void configureEnvironment(EnvPreyVsHunter environment) {
		super.configureEnvironment(environment);
		environment.setExperienceBuilder(new TransitionExperienceBuilder());
	}
	
	@Override
	protected void launchHunters() {
		int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;
        
        List<HunterAgentMADDPG> hunters = new ArrayList<>();
//        ReadOnlyCriterion readOnlyEvaluationCriterion = ((MLKScheduler) getScheduler()).getReadOnlyEvaluationCriterion();

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
        	HunterAgentMADDPG hunter = new HunterAgentMADDPG(maxVisibleHunters, maxVisiblePreys, HUNTER_SPEED, NB_HUNTER_AGENTS, null);
        	hunters.add(hunter);

        }
        for (HunterAgentMADDPG hunter : hunters) {
        	hunter.setOtherAgents(hunters);
        	launchAgent(hunter);
        }

	}
	
	public static void main(String[] args) {
        executeThisAgent(
                "--agentLogLevel", "INFO",
                "--start"
        );
    }

}