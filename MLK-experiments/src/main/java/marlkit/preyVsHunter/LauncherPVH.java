package marlkit.preyVsHunter;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;
import learning.algorithm.Sarsa;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerPVH.class, environment = EnvPreyVsHunter.class, model = MLKModel.class, viewers = {
        ViewerPVH.class })
public class LauncherPVH extends MLKLauncher {

    @Override
    protected void onLaunchSimulatedAgents() {

        List<Action> possibleHunterActions = new ArrayList<>(Move2D.getVonNeumannmove());
        List<Action> possiblePreyActions = new ArrayList<>(Move2D.getVonNeumannmove());

        int nbHunterAgents = 2;
        int nbPreyAgents = 1 ;

        for (int i = 0; i < nbHunterAgents; i++) {
            QValueBasedPolicy policy = new QValueBasedPolicy(possibleHunterActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.001));
        	Sarsa algorithm = new Sarsa(policy, 0.2, 0.95);
            AgentStandard ag = new HunterAgent(policy, algorithm);
            launchAgent(ag);
        }
        for (int i = 0; i < nbPreyAgents; i++) {
        	QValueBasedPolicy policy = new QValueBasedPolicy(possiblePreyActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.001));
        	Sarsa algorithm = new Sarsa(policy, 0.2, 0.95);
            AgentStandard ag = new PreyAgent(policy, algorithm);
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



