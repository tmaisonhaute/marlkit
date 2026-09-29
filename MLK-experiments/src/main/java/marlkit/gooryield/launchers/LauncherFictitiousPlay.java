package marlkit.gooryield.launchers;

import java.util.ArrayList;
import java.util.List;

import madkit.simulation.EngineAgents;
import marlkit.gooryield.SchedulerGoOrYield;
import marlkit.gooryield.ViewerGoOrYield;
import marlkit.gooryield.agent.AgentGoOrYieldFictitiousPlay;
import marlkit.gooryield.environment.EnvGoOrYield;
import simulation.MLKLauncher;
import simulation.LauncherMetadata;
import simulation.MLKModel;

@LauncherMetadata(
		title = "Two Fictitious-Play Agents",
		documentationAnchor = "two-fictitious-play-agents")
@EngineAgents(
		scheduler = SchedulerGoOrYield.class,
		environment = EnvGoOrYield.class,
		model = MLKModel.class,
		viewers = { ViewerGoOrYield.class })
public class LauncherFictitiousPlay extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {

		List<AgentGoOrYieldFictitiousPlay> agents = new ArrayList<>();
		int nbAgents = 2;

		for (int i = 0; i < nbAgents; i++) {
		        agents.add(new AgentGoOrYieldFictitiousPlay());
		    }

	    for (AgentGoOrYieldFictitiousPlay agent : agents) {
	        agent.setOtherAgents(agents);
	        launchAgent(agent);
	    }
	}

	public static void main(String[] args) {
		executeThisAgent(
				"--agentLogLevel","INFO",
				"--start");
	}
}
