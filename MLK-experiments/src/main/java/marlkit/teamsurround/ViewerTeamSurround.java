package marlkit.teamsurround;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.INDIANRED;
import static javafx.scene.paint.Color.LIGHTGRAY;
import static javafx.scene.paint.Color.STEELBLUE;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.Map;

import agent.MLKAgent;
import environment.state.State2DGridInt;
import madkit.simulation.PropertyProbe;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

/**
 * 2D viewer for the TeamSurround environment.
 */
public class ViewerTeamSurround extends Viewer2D {

	private static final double CELL_SIZE = 40;
	private static final double AGENT_SIZE = 26;

	private PropertyProbe<State2DGridInt> stateProbe;

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
	public EnvTeamSurround getEnvironment() {
		return super.getEnvironment();
	}

	@Override
	public void render() {
		super.render();
		drawGrid();
		if (!stateProbe.getAgents().isEmpty()) {
			renderAliveAgents();
			drawHud();
		}
	}

	private void drawGrid() {
		getGraphics().setFill(LIGHTGRAY);
		getGraphics().fillRect(0, 0, getEnvironment().getWidth() * CELL_SIZE, getEnvironment().getHeight() * CELL_SIZE);
		for (int x = 0; x <= getEnvironment().getWidth(); x++) {
			getGraphics().setStroke(BLACK);
			getGraphics().strokeLine(x * CELL_SIZE, 0, x * CELL_SIZE, getEnvironment().getHeight() * CELL_SIZE);
		}
		for (int y = 0; y <= getEnvironment().getHeight(); y++) {
			getGraphics().setStroke(BLACK);
			getGraphics().strokeLine(0, y * CELL_SIZE, getEnvironment().getWidth() * CELL_SIZE, y * CELL_SIZE);
		}
	}

	private void renderAliveAgents() {
		Map<MLKAgent, Pair<Integer, Integer>> alivePositions = getEnvironment().getAliveAgentsPositions();
		for (Map.Entry<MLKAgent, Pair<Integer, Integer>> entry : alivePositions.entrySet()) {
			MLKAgent agent = entry.getKey();
			Pair<Integer, Integer> pos = entry.getValue();
			double x = pos.getFirst() * CELL_SIZE + (CELL_SIZE - AGENT_SIZE) / 2.0;
			double y = pos.getSecond() * CELL_SIZE + (CELL_SIZE - AGENT_SIZE) / 2.0;
			if (getEnvironment().getTeamAt(agent) == 1) {
				getGraphics().setFill(STEELBLUE);
			} else {
				getGraphics().setFill(INDIANRED);
			}
			getGraphics().fillOval(x, y, AGENT_SIZE, AGENT_SIZE);
			getGraphics().setStroke(BLACK);
			getGraphics().strokeOval(x, y, AGENT_SIZE, AGENT_SIZE);
		}
	}

	private void drawHud() {
		getGraphics().setFill(BLACK);
		String txt = "Team1 alive: " + getEnvironment().getAliveCountTeam1()
				+ " | Team2 alive: " + getEnvironment().getAliveCountTeam2()
				+ " | stochasticDeath=" + getEnvironment().isStochasticDeath();
		getGraphics().fillText(txt, 10, 18);
	}
}
