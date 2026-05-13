package marlkit.trade2d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.DARKGRAY;
import static javafx.scene.paint.Color.GRAY;
import static javafx.scene.paint.Color.LIGHTBLUE;
import static javafx.scene.paint.Color.LIGHTGREEN;
import static javafx.scene.paint.Color.LIGHTSALMON;
import static javafx.scene.paint.Color.RED;
import javafx.scene.text.Font;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;
import madkit.simulation.viewer.Viewer2D;
import marlkit.trade.ActionRequestResource;
import marlkit.trade.ResourceType;
import util.Position;

/**
 * 2D viewer for the Trade2D environment.
 */
public class ViewerTrade2D extends Viewer2D {
	private static final double SCALE = 50;
	private static final double AGENT_SIZE = 26;
	private static final double UNIT_SIZE = 32;
	private static final int FONT_SIZE = 12;

	private PropertyProbe<StateUnites2D> stateProbe;
	private PropertyProbe<Map<MLKAgent, Action>> lastActionsProbe;
	private Map<ResourceType, Color> resourceColors;

	/**
	 * Initialize probes, colors, and canvas size.
	 */
	@Override
	protected void onActivation() {
		super.onActivation();
		stateProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "state");
		lastActionsProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "lastActions");
		addProbe(stateProbe);
		addProbe(lastActionsProbe);
		initializeResourceColors();
		getGUI().getCanvas().setWidth(getEnvironment().getWidth() * SCALE);
		getGUI().getCanvas().setHeight(getEnvironment().getHeight() * SCALE);
		getGUI().setSynchroPainting(false);
	}

	/**
	 * Return the Trade2D environment instance.
	 *
	 * @return Environment instance.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EnvTrade2D getEnvironment() {
		return super.getEnvironment();
	}

	/**
	 * Render the background, units, agents, and request connections.
	 */
	@Override
	public void render() {
		super.render();
		drawBackground();
		List<Agent> envs = stateProbe.getAgents();
		for (Agent env : envs) {
			StateUnites2D state = stateProbe.getPropertyValue(env);
			Map<MLKAgent, Action> lastActions = lastActionsProbe.getPropertyValue(env);
			drawUnits(state);
			drawAgents(state);
			if (lastActions != null) {
				drawConnections(state, lastActions);
			}
		}
	}

	/**
	 * Initialize color mapping for resource types.
	 */
	private void initializeResourceColors() {
		resourceColors = new HashMap<>();
		resourceColors.put(ResourceType.A, LIGHTBLUE);
		resourceColors.put(ResourceType.B, LIGHTGREEN);
		resourceColors.put(ResourceType.C, LIGHTSALMON);
	}

	/**
	 * Draw the grid background and cell boundaries.
	 */
	private void drawBackground() {
		getGraphics().setFill(GRAY);
		getGraphics().fillRect(0, 0, getEnvironment().getWidth() * SCALE, getEnvironment().getHeight() * SCALE);
	}

	/**
	 * Draw production units and their stock values.
	 *
	 * @param state Current environment state.
	 */
	private void drawUnits(StateUnites2D state) {
		getGraphics().setFont(new Font(FONT_SIZE));
		for (UniteProductionSpatial unit : state.getUnitesProductions()) {
			Position pos = unit.getPosition();
			if (pos == null) {
				continue;
			}
			double x = pos.x * SCALE - UNIT_SIZE / 2.0;
			double y = pos.y * SCALE - UNIT_SIZE / 2.0;
			Color color = resourceColors.getOrDefault(unit.getResourceType(), GRAY);
			getGraphics().setFill(color);
			getGraphics().fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
			getGraphics().setStroke(BLACK);
			getGraphics().strokeRect(x, y, UNIT_SIZE, UNIT_SIZE);
			getGraphics().setFill(BLACK);
			getGraphics().fillText(String.valueOf(unit.getStockValue()), x + 6, y + UNIT_SIZE - 6);
		}
	}

	/**
	 * Draw agents at their positions.
	 *
	 * @param state Current environment state.
	 */
	private void drawAgents(StateUnites2D state) {
		for (MLKAgent agent : state.getAgents()) {
			Position pos = state.getAgentPosition(agent);
			if (pos == null) {
				continue;
			}
			double x = pos.x * SCALE - AGENT_SIZE / 2.0;
			double y = pos.y * SCALE - AGENT_SIZE / 2.0;
			getGraphics().setFill(RED);
			getGraphics().fillOval(x, y, AGENT_SIZE, AGENT_SIZE);
			getGraphics().setStroke(BLACK);
			getGraphics().strokeOval(x, y, AGENT_SIZE, AGENT_SIZE);
		}
	}

	/**
	 * Draw lines from agents to their requested units.
	 *
	 * @param state Current environment state.
	 * @param lastActions Last action map.
	 */
	private void drawConnections(StateUnites2D state, Map<MLKAgent, Action> lastActions) {
		for (Map.Entry<MLKAgent, Action> entry : lastActions.entrySet()) {
			MLKAgent agent = entry.getKey();
			Action action = entry.getValue();
			if (!(action instanceof ActionRequestResource arr)) {
				continue;
			}
			Position agentPos = state.getAgentPosition(agent);
			Position unitPos = ((UniteProductionSpatial) arr.getUniteProduction()).getPosition();
			if (agentPos == null || unitPos == null) {
				continue;
			}
			double ax = agentPos.x * SCALE;
			double ay = agentPos.y * SCALE;
			double ux = unitPos.x * SCALE;
			double uy = unitPos.y * SCALE;
			getGraphics().setStroke(DARKGRAY);
			getGraphics().setLineWidth(1.5);
			getGraphics().strokeLine(ax, ay, ux, uy);
		}
	}
}
