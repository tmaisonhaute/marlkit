package marlkit.pushTheBlock;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;
import learning.policy.PolicyRandom;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;
import simulation.MLKSchedulerStandard;
import util.Pair;

@EngineAgents(
        scheduler=MLKSchedulerStandard.class,
        environment = EnvPushTheBlock.class,
        model = MLKModel.class,
        viewers = {ViewerPTB.class}
)
public class LauncherPTB extends MLKLauncher{

	@Override
	protected void onLaunchSimulatedAgents() {
		Action goLeft = new Action2DMove(new Pair<>(0, -1));
		Action goRight = new Action2DMove(new Pair<>(0, 1));
		Action goUp = new Action2DMove(new Pair<>(-1, 0));
		Action goDown = new Action2DMove(new Pair<>(1, 0));
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		PolicyRandom policy = new PolicyRandom(possibleActions);
		AgentStandard ag = new AgentStandard(policy);
		launchAgent(ag);
		
		((MLKSchedulerStandard) getScheduler()).agentShareInformation();
		throw new RuntimeException("on est dans le mlk launcher");
		
	}
	@Override
	public void onSimulationStart() {
	}
	
	public static void main(String[] args) {
		executeThisAgent(
				"--agentLogLevel", "ALL"
				, "--start"
//				,"--viewers",MyViewer.class.getName()
        );
    }
	

}
