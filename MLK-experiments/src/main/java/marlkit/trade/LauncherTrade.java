package marlkit.trade;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import rewardmodelimplementation.FullyCooperativeReward;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerTrade.class, model = MLKModel.class, viewers = {
		ViewerTrade.class })
public class LauncherTrade extends MLKLauncher {

	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
//		EnvTrade env = new EnvTrade(800, 600, new MixedReward(),new Scenario4());
		EnvTrade env = new EnvTrade(800, 600, new FullyCooperativeReward(),new Scenario4());
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}
	
	@Override
	protected void onLaunchSimulatedAgents() {

        EnvTrade env = getEnvironment();
        StateUnites state = (StateUnites) env.getState();
        List<ProductionUnit> unites = state.getUnitesProductions();
		
		List<Action> possibleActions = new ArrayList<>();
		for (ProductionUnit unite : unites) {
			possibleActions.add(new ActionRequestResource(unite));
		}
		
		int nbAgents = 3;
		
		for (int i = 0; i < nbAgents; i++) {
			QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.005));
        	QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.95);
			AgentStandard ag = new AgentStandard(policy, algorithm);
			launchAgent(ag);
		}
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO"
//				,"--noLog"
				, "--start"
//				,"--viewers",ViewerTrade.class.getName()
		);
	}

}

