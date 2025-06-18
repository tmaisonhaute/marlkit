package marlkit.preyVsHunter;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;
import learning.policy.PolicyMonteCarlo;
import learning.policy.PolicySarsa;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

import java.util.ArrayList;
import java.util.List;

@EngineAgents(scheduler = SchedulerPVH.class, environment = EnvPreyVsHunter.class, model = MLKModel.class, viewers = {
        ViewerPVH.class })
public class LauncherPVH extends MLKLauncher {

    @Override
    protected void onLaunchSimulatedAgents() {
        Action goLeft = Action2DMove.left();
        Action goRight = Action2DMove.right();
        Action goUp = Action2DMove.up();
        Action goDown = Action2DMove.down();
        Action doNothing = Action2DMove.idle();

        List<Action> possibleHunterActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));
        List<Action> possiblePreyActions = new ArrayList<>(List.of(goLeft, goRight, goUp, goDown));

        int nbHunterAgents = 2;
        int nbPreyAgents = 1 ;

        for (int i = 0; i < nbHunterAgents; i++) {
            PolicySarsa hunterPolicy = new PolicySarsa(possibleHunterActions, 1.0, 0.0002);
            AgentStandard ag = new HunterAgent(hunterPolicy);
            launchAgent(ag);
        }
        for (int i = 0; i < nbPreyAgents; i++) {
            PolicySarsa PreyPolicy = new PolicySarsa(possiblePreyActions, 1.0, 0.0002);
            AgentStandard ag = new PreyAgent(PreyPolicy);
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



