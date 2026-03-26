package marlkit.crossescape;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.DARKGRAY;
import static javafx.scene.paint.Color.DEEPSKYBLUE;
import static javafx.scene.paint.Color.GOLD;
import static javafx.scene.paint.Color.WHITESMOKE;

import java.util.List;
import java.util.Map;

import environment.state.State2DGridInt;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

public class ViewerCrossEscape extends Viewer2D {

	private static final double CELL_SIZE = 55;
	private static final double AGENT_SIZE = 40;
	private static final double GOAL_SIZE = 16;

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
	public EnvCrossEscape getEnvironment() {
		return super.getEnvironment();
	}

	@Override
	public void render() {
		super.render();
		for (Agent env : stateProbe.getAgents()) {
			State2DGridInt state = stateProbe.getPropertyValue(env);
			renderGrid(state);
			renderGoals();
			renderAgents(state);
		}
	}

	private void renderGrid(State2DGridInt state) {
		for (int x = 0; x < state.getWidth(); x++) {
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getValue(x, y) < 0) {
					getGraphics().setFill(DARKGRAY);
				} else {
					getGraphics().setFill(WHITESMOKE);
				}
				getGraphics().fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
				getGraphics().setStroke(BLACK);
				getGraphics().strokeRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
			}
		}
	}

	private void renderGoals() {
		Map<?, Pair<Integer, Integer>> goals = getEnvironment().getGoalPositions();
		for (Pair<Integer, Integer> goal : goals.values()) {
			double x = goal.getFirst() * CELL_SIZE + (CELL_SIZE - GOAL_SIZE) / 2.0;
			double y = goal.getSecond() * CELL_SIZE + (CELL_SIZE - GOAL_SIZE) / 2.0;
			getGraphics().setFill(GOLD);
			getGraphics().fillRect(x, y, GOAL_SIZE, GOAL_SIZE);
		}
	}

	private void renderAgents(State2DGridInt state) {
		List<Pair<Integer, Integer>> positions = state.getAgentsPositions().values().stream().toList();
		for (Pair<Integer, Integer> position : positions) {
			double x = position.getFirst() * CELL_SIZE + (CELL_SIZE - AGENT_SIZE) / 2.0;
			double y = position.getSecond() * CELL_SIZE + (CELL_SIZE - AGENT_SIZE) / 2.0;
			getGraphics().setFill(DEEPSKYBLUE);
			getGraphics().fillOval(x, y, AGENT_SIZE, AGENT_SIZE);
			getGraphics().setStroke(BLACK);
			getGraphics().strokeOval(x, y, AGENT_SIZE, AGENT_SIZE);
		}
	}
}
