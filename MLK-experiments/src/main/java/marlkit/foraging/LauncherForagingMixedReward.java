package marlkit.foraging;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;
import learning.algorithm.QLearning;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyPowerDecay;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import rewardmodels.MixedReward;
import simulation.MLKLauncher;
import simulation.MLKModel;


@EngineAgents(scheduler = SchedulerForaging.class, model = MLKModel.class, viewers = {
		ViewerForaging.class })
public class LauncherForagingMixedReward extends MLKLauncher {

	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvForaging env = new EnvForaging(5, 6, new ScenarioDeterministic1(), new MixedReward());
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}
	
	@Override
	protected void onLaunchSimulatedAgents() {
		Action goLeft = Action2DMove.left(); 
		Action goRight = Action2DMove.right();
		Action goUp = Action2DMove.up(); 
		Action goDown = Action2DMove.down();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		int nbAgents = 2;
		
		for (int i = 0; i < nbAgents; i++) {
			QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyPowerDecay(0.5));
        	QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.995);

			AgentStandard ag = new AgentStandard(policy, algorithm);
			launchAgent(ag);
		}
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel"
				, "INFO"
//				,"--noLog"
				, "--start"
//				,"--viewers",MyViewer.class.getName()
		);
	}

}
