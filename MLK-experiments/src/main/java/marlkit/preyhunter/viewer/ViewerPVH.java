package marlkit.preyhunter.viewer;

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
import marlkit.preyhunter.environment.EnvPreyVsHunter;
import marlkit.preyhunter.environment.StatePreyHunter2D;
import util.Pair;

/**
 * 2D viewer for the continuous PreyHunter environment.
 */
public class ViewerPVH extends Viewer2D {

    private static final double SCALE = 50.0;

    private static final double HUNTER_SIZE = 28.0;
    private static final double PREY_SIZE = 24.0;

    private static final boolean DRAW_GRID = true;

    private PropertyProbe<StatePreyHunter2D> stateProbe;

    /**
     * Initialize probes and canvas.
     */
    @Override
    protected void onActivation() {
        super.onActivation();

        stateProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "state");
        addProbe(stateProbe);

        getGUI().getCanvas().setWidth(getEnvironment().getWidth() * SCALE);
        getGUI().getCanvas().setHeight(getEnvironment().getHeight() * SCALE);
        getGUI().setSynchroPainting(false);
    }

    /**
     * Return the PreyHunter environment.
     *
     * @return environment instance
     */
    @SuppressWarnings("unchecked")
    @Override
    public EnvPreyVsHunter getEnvironment() {
        return super.getEnvironment();
    }

    /**
     * Render the continuous environment.
     */
    @Override
    public void render() {
        super.render();

        drawBackground();

        if (DRAW_GRID) {
            drawGrid();
        }

        List<Agent> envs = stateProbe.getAgents();

        for (Agent env : envs) {
            StatePreyHunter2D state = stateProbe.getPropertyValue(env);

            if (state == null) {
                continue;
            }

            drawPreys(state);
            drawHunters(state);
        }
    }

    /**
     * Draw white background.
     */
    private void drawBackground() {
        getGraphics().setFill(WHITE);
        getGraphics().fillRect(
                0,
                0,
                getEnvironment().getWidth() * SCALE,
                getEnvironment().getHeight() * SCALE
        );
    }

    /**
     * Draw a light grid as visual reference.
     */
    private void drawGrid() {
        getGraphics().setStroke(LIGHTGRAY);
        getGraphics().setLineWidth(0.5);

        for (int x = 0; x <= getEnvironment().getWidth(); x++) {
            double px = x * SCALE;
            getGraphics().strokeLine(
                    px,
                    0,
                    px,
                    getEnvironment().getHeight() * SCALE
            );
        }

        for (int y = 0; y <= getEnvironment().getHeight(); y++) {
            double py = y * SCALE;
            getGraphics().strokeLine(
                    0,
                    py,
                    getEnvironment().getWidth() * SCALE,
                    py
            );
        }
    }

    /**
     * Draw prey agents.
     *
     * @param state current state
     */
    private void drawPreys(StatePreyHunter2D state) {
        Map<MLKAgent, Pair<Double, Double>> preysPositions = state.getPreysPositions();

        for (Map.Entry<MLKAgent, Pair<Double, Double>> entry : preysPositions.entrySet()) {
            drawAgent(entry.getValue(), PREY_SIZE, RED);
        }
    }

    /**
     * Draw hunter agents.
     *
     * @param state current state
     */
    private void drawHunters(StatePreyHunter2D state) {
        Map<MLKAgent, Pair<Double, Double>> huntersPositions = state.getHuntersPositions();

        for (Map.Entry<MLKAgent, Pair<Double, Double>> entry : huntersPositions.entrySet()) {
            drawAgent(entry.getValue(), HUNTER_SIZE, BLUE);
        }
    }

    /**
     * Draw one agent at a continuous position.
     *
     * @param position continuous position
     * @param size displayed size
     * @param color fill color
     */
    private void drawAgent(Pair<Double, Double> position, double size, Color color) {
        if (position == null) {
            return;
        }

        double x = position.getFirst() * SCALE - size / 2.0;
        double y = position.getSecond() * SCALE - size / 2.0;

        getGraphics().setFill(color);
        getGraphics().fillOval(x, y, size, size);

        getGraphics().setStroke(BLACK);
        getGraphics().setLineWidth(1.0);
        getGraphics().strokeOval(x, y, size, size);
    }
}