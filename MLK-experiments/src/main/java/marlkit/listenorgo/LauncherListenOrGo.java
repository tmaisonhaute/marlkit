package marlkit.listenorgo;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

/**
 * Launcher for the ListenOrGo multi-agent simulation.
 * <p>
 * Configures and starts a set of agents, each equipped with a
 * {@link learning.algorithms.QLearning Q-Learning} algorithm and an
 * epsilon-greedy exploration policy with exponential decay.
 * </p>
 */
@EngineAgents(
    scheduler = SchedulerListenOrGo.class, 
    environment = EnvListenOrGo.class, 
    model = MLKModel.class, 
    viewers = { ViewerListenOrGo.class }
)
public class LauncherListenOrGo extends MLKLauncher {
    
    /**
     * Creates and launches all agents into the simulation.
     * <p>
     * Each agent is given the three available actions ({@link ActionListen},
     * {@link ActionGoLeft}, {@link ActionGoRight}) and learns via Q-Learning
     * with an epsilon-greedy policy using exponential decay.
     * </p>
     */
    @Override
    protected void onLaunchSimulatedAgents() {
        Action listen = new ActionListen();
        Action goLeft = new ActionGoLeft();
        Action goRight = new ActionGoRight();
        
        List<Action> possibleActions = new ArrayList<>(List.of(listen, goLeft, goRight));
        
        int nbAgents = 5;
        for (int i = 0; i < nbAgents; i++) {
        	QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyExponentialDecay(1.0, 0.001));
        	QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.95);
            AgentStandard agent = new AgentStandard(policy, algorithm);
            launchAgent(agent);
        }
    }
    
    /**
     * Entry point to launch the ListenOrGo simulation.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        executeThisAgent(
            "--agentLogLevel"
            // , "INFO"
            , "WARNING"
            ,"--start"
        );
    }
}
