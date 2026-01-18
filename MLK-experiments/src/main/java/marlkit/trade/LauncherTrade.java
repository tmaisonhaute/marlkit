package marlkit.trade;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.policy.deprecated.PolicyQLearning;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerTrade.class, environment = EnvTrade.class, model = MLKModel.class, viewers = {
		ViewerTrade.class })
public class LauncherTrade extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {

        EnvTrade env = getEnvironment();
        StateUnites state = (StateUnites) env.getState();
        List<UniteProduction> unites = state.getUnitesProductions();
		
		// Create actions - one for each production unit
		List<Action> possibleActions = new ArrayList<>();
		for (UniteProduction unite : unites) {
			possibleActions.add(new ActionRequestResource(unite));
		}
		
		int nbAgents = 2;
		
		for (int i = 0; i < nbAgents; i++) {
			PolicyQLearning policy = new PolicyQLearning(possibleActions, 1.0, 0.001, 1.0, 0.2, 0.95);
			AgentStandard ag = new AgentStandard(policy);
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
