package marlkit.trade;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.algorithm.QLearning;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedy;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerTrade.class, model = MLKModel.class, viewers = {
		ViewerTrade.class })
public class LauncherTrade extends MLKLauncher {

	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvTrade env = new EnvTrade(800, 600, new RewardConfigurationMixed(),new Scenario7());
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}
	
	@Override
	protected void onLaunchSimulatedAgents() {

        EnvTrade env = getEnvironment();
        StateUnites state = (StateUnites) env.getState();
        List<UniteProduction> unites = state.getUnitesProductions();
		
		List<Action> possibleActions = new ArrayList<>();
		for (UniteProduction unite : unites) {
			possibleActions.add(new ActionRequestResource(unite));
		}
		
		int nbAgents = 9;
		
		for (int i = 0; i < nbAgents; i++) {
			QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedy(1.0, 0.005));
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

//class EnvTrade1 extends EnvTrade {
//    public EnvTrade1() {
//        super(800, 600, new RewardConfigurationMixed(), new IndependantLearning(), new Scenario1());
//    }
//}
