package marlkit.preyhuntergrid;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.LIGHTGRAY;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.WHITE;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import javafx.scene.paint.Color;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

/**
 * Viewer for the grid-based PreyHunter environment.
 */
public class ViewerPVHGrid extends Viewer2D {

    private static final double CELL_SIZE = 50.0;
    private static final double HUNTER_SIZE = 34.0;
    private static final double PREY_SIZE = 30.0;

    private PropertyProbe<StatePreyHunterGrid> stateProbe;

    @Override
    protected void onActivation() {
        super.onActivation();

        stateProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "state");
        addProbe(stateProbe);

        getGUI().getCanvas().setWidth(getEnvironment().getWidth() * CELL_SIZE);
        getGUI().getCanvas().setHeight(getEnvironment().getHeight() * CELL_SIZE);
        getGUI().setSynchroPainting(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EnvPreyVsHunterGrid getEnvironment() {
        return super.getEnvironment();
    }

    @Override
    public void render() {
        super.render();

        drawBackground();
        drawGrid();

        List<Agent> envs = stateProbe.getAgents();

        for (Agent env : envs) {
            StatePreyHunterGrid state = stateProbe.getPropertyValue(env);

            if (state == null) {
                continue;
            }

            drawPreys(state);
            drawHunters(state);
        }
    }

    private void drawBackground() {
        getGraphics().setFill(WHITE);
        getGraphics().fillRect(
                0,
                0,
                getEnvironment().getWidth() * CELL_SIZE,
                getEnvironment().getHeight() * CELL_SIZE
        );
    }

    private void drawGrid() {
        getGraphics().setStroke(LIGHTGRAY);
        getGraphics().setLineWidth(0.5);

        for (int x = 0; x <= getEnvironment().getWidth(); x++) {
            double px = x * CELL_SIZE;
            getGraphics().strokeLine(
                    px,
                    0,
                    px,
                    getEnvironment().getHeight() * CELL_SIZE
            );
        }

        for (int y = 0; y <= getEnvironment().getHeight(); y++) {
            double py = y * CELL_SIZE;
            getGraphics().strokeLine(
                    0,
                    py,
                    getEnvironment().getWidth() * CELL_SIZE,
                    py
            );
        }
    }

    private void drawPreys(StatePreyHunterGrid state) {
        Map<MLKAgent, Pair<Integer, Integer>> preysPositions = state.getPreysPositions();

        for (Pair<Integer, Integer> position : preysPositions.values()) {
            drawAgent(position, PREY_SIZE, RED);
        }
    }

    private void drawHunters(StatePreyHunterGrid state) {
        Map<MLKAgent, Pair<Integer, Integer>> huntersPositions = state.getHuntersPositions();

        for (Pair<Integer, Integer> position : huntersPositions.values()) {
            drawAgent(position, HUNTER_SIZE, BLUE);
        }
    }

    private void drawAgent(Pair<Integer, Integer> position, double size, Color color) {
        if (position == null) {
            return;
        }

        double centerX = position.getFirst() * CELL_SIZE + CELL_SIZE / 2.0;
        double centerY = position.getSecond() * CELL_SIZE + CELL_SIZE / 2.0;

        double x = centerX - size / 2.0;
        double y = centerY - size / 2.0;

        getGraphics().setFill(color);
        getGraphics().fillOval(x, y, size, size);

        getGraphics().setStroke(BLACK);
        getGraphics().setLineWidth(1.0);
        getGraphics().strokeOval(x, y, size, size);
    }
}