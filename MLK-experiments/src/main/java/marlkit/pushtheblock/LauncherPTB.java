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

@EngineAgents(scheduler = MLKSchedulerStandard.class, environment = EnvPushTheBlock.class, model = MLKModel.class, viewers = {
		ViewerPTB.class })
public class LauncherPTB extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		Action goLeft = Action2DMove.left(); 
		Action goRight = Action2DMove.right();
		Action goUp = Action2DMove.up(); 
		Action goDown = Action2DMove.down();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		int nbAgents = 1;
		for (int i = 0; i < nbAgents; i++) {
			PolicyMonteCarlo policy = new PolicyMonteCarlo(possibleActions, 1.0, 0.001);
//			PolicySarsa policy = new PolicySarsa(possibleActions, 1.0, 0.0003);
			AgentStandard ag = new AgentStandard(policy);
			launchAgent(ag);
		}


	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO"
//				,"--noLog"
				, "--start"
//				,"--viewers",MyViewer.class.getName()
		);
	}

}
