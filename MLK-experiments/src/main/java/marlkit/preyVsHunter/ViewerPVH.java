package marlkit.preyVsHunter;

import agent.MLKAgent;
import environment.state.State2DGridInt;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

import java.util.List;

import static javafx.scene.paint.Color.*;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

public class ViewerPVH extends Viewer2D {
    PropertyProbe<State2DGridInt> stateProbe;
    private static final double CELLSIZE = 50;
    private static final double AGENTSIZE = 50;

    @Override
    protected void onActivation() {
        super.onActivation();
        stateProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "state");
        addProbe(stateProbe);
        getGUI().getCanvas().setWidth(getEnvironment().getWidth() * CELLSIZE);
        getGUI().getCanvas().setHeight(getEnvironment().getHeight() * CELLSIZE);
        getGUI().setSynchroPainting(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EnvPreyVsHunter getEnvironment() {
        return super.getEnvironment();
    }

    @Override
    public void render() {
        super.render();
        List<Agent> envs = stateProbe.getAgents();
        for (Agent env : envs) {
            State2DGridInt s = stateProbe.getPropertyValue(env);
            for (MLKAgent ag: s.getAgentsPositions().keySet()) {
                Pair<Integer, Integer> pos = s.getAgentsPositions().get(ag);
                if (ag instanceof PreyAgent) {
                getGraphics().setFill(RED);
                getGraphics().fillOval(pos.getFirst() * CELLSIZE, pos.getSecond() * CELLSIZE, AGENTSIZE, AGENTSIZE);

                } else if (ag instanceof HunterAgent) {
                    getGraphics().setFill(BLUE);
                    getGraphics().fillOval(pos.getFirst() * CELLSIZE, pos.getSecond() * CELLSIZE, AGENTSIZE, AGENTSIZE);
                }
            }
        }

        for (int i = 0; i < getEnvironment().getWidth(); i++) {
            getGraphics().setStroke(BLACK);
            getGraphics().strokeLine(i * CELLSIZE, 0, i * CELLSIZE, getEnvironment().getHeight() * CELLSIZE);
            getGraphics().strokeLine(0, i * CELLSIZE, getEnvironment().getWidth() * CELLSIZE, i * CELLSIZE);

        }


    }
}