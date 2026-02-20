package marlkit.pushtheblock;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;
import learning.algorithm.QLearning;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;


@EngineAgents(scheduler = SchedulerPTB.class, environment = EnvPushTheBlock.class, model = MLKModel.class, viewers = {
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
			QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.001));
        	QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.95);
//        	ActorNetwork policy = new ActorNetwork(4, 20, 
//        			new WrapperVectorObservationPositionsValues(false), possibleActions);
//        	Reinforce algorithm = new Reinforce(policy, 0.01, 0.95);

			AgentStandard ag = new AgentStandard(policy, algorithm);
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



