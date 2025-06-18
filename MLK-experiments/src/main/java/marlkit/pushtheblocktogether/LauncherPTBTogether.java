package marlkit.pushtheblocktogether;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;
import learning.policy.PolicyMonteCarlo;
import madkit.simulation.EngineAgents;
import marlkit.pushtheblock.SchedulerPTB;
import marlkit.pushtheblock.ViewerPTB;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerPTB.class, environment = EnvPushTheBlockTogether.class, model = MLKModel.class, viewers = {
		ViewerPTB.class })
public class LauncherPTBTogether extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		Action goLeft = Action2DMove.left(); 
		Action goRight = Action2DMove.right();
		Action goUp = Action2DMove.up(); 
		Action goDown = Action2DMove.down();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		int nbAgents = 2;
		for (int i = 0; i < nbAgents; i++) {
			PolicyMonteCarlo policy = new PolicyMonteCarlo(possibleActions, 1.0, 0.001);
//			PolicySarsa policy = new PolicySarsa(possibleActions, 1.0, 0.0003);
//			PolicyActorCritic policy = new PolicyActorCritic(possibleActions, 4);
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
