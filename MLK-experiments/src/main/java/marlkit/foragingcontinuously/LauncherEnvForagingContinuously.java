package marlkit.foragingcontinuously;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyPowerDecay;
import learning.policies.SoftmaxQPolicy;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.foraging.EnvForaging;
import marlkit.foraging.SchedulerForaging;
import marlkit.foraging.ViewerForaging;
import marlkit.foragingcontinuously.scenario.ScenarioContinuously;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerForaging.class, model = MLKModel.class, viewers = {
		ViewerForaging.class })
public class LauncherEnvForagingContinuously extends MLKLauncher {

	@SuppressWarnings("unchecked")
	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvForaging env = new EnvForagingContinuously(4, 4, new ScenarioContinuously(50, 1));
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}
	
	@Override
	protected void onLaunchSimulatedAgents() {
		Action goLeft = Move2D.left(); 
		Action goRight = Move2D.right();
		Action goUp = Move2D.up(); 
		Action goDown = Move2D.down();
		List<Action> possibleActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
		int nbAgents = 2;
		
		for (int i = 0; i < nbAgents; i++) {
//			QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyPowerDecay(0.5));
			SoftmaxQPolicy policy = new SoftmaxQPolicy(possibleActions, 1.0, 3, new EpsilonGreedyPowerDecay(0.5));
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
