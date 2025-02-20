package marlkit.pushtheblock;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;
import learning.policy.PolicyMonteCarlo;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;
import simulation.MLKSchedulerStandard;
import util.Pair;

@EngineAgents(scheduler = MLKSchedulerStandard.class, environment = EnvPushTheBlock.class, model = MLKModel.class, viewers = {
		ViewerPTB.class })
public class LauncherPTB extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		Action goLeft = new Action2DMove(new Pair<>(0, -1));
		Action goRight = new Action2DMove(new Pair<>(0, 1));
		Action goUp = new Action2DMove(new Pair<>(-1, 0));
		Action goDown = new Action2DMove(new Pair<>(1, 0));
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		int nbAgents = 1;
		for (int i = 0; i < nbAgents; i++) {
//			PolicyRandom policy = new PolicyRandom(possibleActions);
			PolicyMonteCarlo policy = new PolicyMonteCarlo(possibleActions);
			AgentStandard ag = new AgentStandard(policy);
			launchAgent(ag);
		}

		// TODO why not doing this call on agents in the previous loop?
		((MLKSchedulerStandard) getScheduler()).agentShareInformation();

	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO"
//				,"--noLog"
//				, "--start"
//				,"--viewers",MyViewer.class.getName()
		);
	}

}
