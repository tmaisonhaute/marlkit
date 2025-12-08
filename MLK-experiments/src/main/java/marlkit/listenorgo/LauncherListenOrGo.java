package marlkit.listenorgo;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.policy.PolicyQLearning;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(
    scheduler = SchedulerListenOrGo.class, 
    environment = EnvListenOrGo.class, 
    model = MLKModel.class, 
    viewers = { ViewerListenOrGo.class }
)
public class LauncherListenOrGo extends MLKLauncher {
    
    @Override
    protected void onLaunchSimulatedAgents() {
        Action listen = new ActionListen();
        Action goLeft = new ActionGoLeft();
        Action goRight = new ActionGoRight();
        
        List<Action> possibleActions = new ArrayList<>(List.of(listen, goLeft, goRight));
        
        int nbAgents = 3;
        for (int i = 0; i < nbAgents; i++) {
            PolicyQLearning policy = new PolicyQLearning(possibleActions, 1.0, 0.001, 1.0, 0.2, 0.95);
            AgentStandard agent = new AgentStandard(policy);
            launchAgent(agent);
        }
    }
    
    public static void main(String[] args) {
        executeThisAgent(
            "--agentLogLevel"
            // , "INFO"
            , "WARNING"
            ,"--start"
        );
    }
}
