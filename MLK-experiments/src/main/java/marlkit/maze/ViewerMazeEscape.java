package marlkit.maze;

import java.util.List;

import environment.state.State2DGridInt;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.DARKSLATEGRAY;
import static javafx.scene.paint.Color.HONEYDEW;
import static javafx.scene.paint.Color.LIGHTBLUE;
import static javafx.scene.paint.Color.LIGHTGREEN;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.SIENNA;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

public class ViewerMazeEscape extends Viewer2D {
	private static final double CELL_SIZE = 50;
	private static final double AGENT_SIZE = 42;
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
	public EnvMazeEscape getEnvironment() {
		return super.getEnvironment();
	}

	@Override
	public void render() {
		super.render();
		List<Agent> envs = stateProbe.getAgents();
		for (Agent env : envs) {
			State2DGridInt state = stateProbe.getPropertyValue(env);
			renderCells(state);
			renderAgents(state);
		}
		renderGrid();
	}

	private void renderCells(State2DGridInt state) {
		for (int i = 0; i < state.getWidth(); i++) {
			for (int j = 0; j < state.getHeight(); j++) {
				MazeCellType type = MazeCellType.fromCode(state.getValue(i, j));
				switch (type) {
				case FREE:
					getGraphics().setFill(HONEYDEW);
					break;
				case SPAWN:
					getGraphics().setFill(LIGHTBLUE);
					break;
				case HOLE:
					getGraphics().setFill(DARKSLATEGRAY);
					break;
				case EXIT:
					getGraphics().setFill(LIGHTGREEN);
					break;
				case WALL:
					getGraphics().setFill(SIENNA);
					break;
				default:
					getGraphics().setFill(HONEYDEW);
					break;
				}
				getGraphics().fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
			}
		}
	}

	private void renderAgents(State2DGridInt state) {
		for (Pair<Integer, Integer> pos : state.getAgentsPositions().values()) {
			getGraphics().setFill(RED);
			double x = pos.getFirst() * CELL_SIZE + (CELL_SIZE - AGENT_SIZE) / 2;
			double y = pos.getSecond() * CELL_SIZE + (CELL_SIZE - AGENT_SIZE) / 2;
			getGraphics().fillOval(x, y, AGENT_SIZE, AGENT_SIZE);
		}
	}

	private void renderGrid() {
		for (int i = 0; i <= getEnvironment().getWidth(); i++) {
			getGraphics().setStroke(BLACK);
			getGraphics().strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, getEnvironment().getHeight() * CELL_SIZE);
		}
		for (int j = 0; j <= getEnvironment().getHeight(); j++) {
			getGraphics().setStroke(BLACK);
			getGraphics().strokeLine(0, j * CELL_SIZE, getEnvironment().getWidth() * CELL_SIZE, j * CELL_SIZE);
		}
	}
}
