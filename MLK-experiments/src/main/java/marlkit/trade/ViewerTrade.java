package marlkit.trade;

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

public class ViewerTrade extends Viewer2D {
	PropertyProbe<StateUnites> stateProbe;
	PropertyProbe<Map<MLKAgent, Action>> lastActionsProbe;
	
	private static final double UNIT_WIDTH = 100;
	private static final double UNIT_HEIGHT = 80;
	private static final double AGENT_SIZE = 40;
	private static final double TOP_MARGIN = 20;
	private static final double MIDDLE_GAP = 100;
	private static final int FONT_SIZE = 14;
	
	private Map<ResourceType, Color> resourceColors;

	@Override
	protected void onActivation() {
		super.onActivation();
		stateProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "state");
		lastActionsProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "lastActions");
		addProbe(stateProbe);
		addProbe(lastActionsProbe);
		
		initializeResourceColors();
		
		EnvTrade env = getEnvironment();
		StateUnites state = (StateUnites) env.getState();
		int nbUnits = state.getUnitesProductions().size();
		int nbAgents = state.getAgents().size();
		
		double canvasWidth = Math.max(nbUnits, nbAgents) * (UNIT_WIDTH + 20) + 40;
		double canvasHeight = TOP_MARGIN + UNIT_HEIGHT + MIDDLE_GAP + AGENT_SIZE + 40;
		
		getGUI().getCanvas().setWidth(canvasWidth);
		getGUI().getCanvas().setHeight(canvasHeight);
		getGUI().setSynchroPainting(false);
	}
	
	private void initializeResourceColors() {
		resourceColors = new HashMap<>();
		resourceColors.put(ResourceType.A, LIGHTBLUE);
		resourceColors.put(ResourceType.B, LIGHTGREEN);
		resourceColors.put(ResourceType.C, LIGHTSALMON);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EnvTrade getEnvironment() {
		return super.getEnvironment();
	}

	@Override
	public void render() {
		super.render();
		List<Agent> envs = stateProbe.getAgents();
		
		for (Agent env : envs) {
			StateUnites state = stateProbe.getPropertyValue(env);
			Map<MLKAgent, Action> lastActions = lastActionsProbe.getPropertyValue(env);
			
			List<ProductionUnit> unites = state.getUnitesProductions();
			List<MLKAgent> agents = state.getAgents();
			
			Map<ProductionUnit, Double> unitPositions = drawProductionUnits(unites);
			
			Map<MLKAgent, Double> agentPositions = drawAgents(agents);
			
			if (lastActions != null) {
				drawConnections(agentPositions, unitPositions, lastActions);
			}
		}
	}
	
	private Map<ProductionUnit, Double> drawProductionUnits(List<ProductionUnit> unites) {
		Map<ProductionUnit, Double> positions = new HashMap<>();
		double yPos = TOP_MARGIN;
		
		for (int i = 0; i < unites.size(); i++) {
			ProductionUnit unite = unites.get(i);
			double xPos = 20 + i * (UNIT_WIDTH + 20);
			
			// Draw unit rectangle
			Color color = resourceColors.getOrDefault(unite.getResourceType(), GRAY);
			getGraphics().setFill(color);
			getGraphics().fillRect(xPos, yPos, UNIT_WIDTH, UNIT_HEIGHT);
			
			// Draw border
			getGraphics().setStroke(BLACK);
			getGraphics().setLineWidth(2);
			getGraphics().strokeRect(xPos, yPos, UNIT_WIDTH, UNIT_HEIGHT);
			
			// Draw resource type
			getGraphics().setFill(BLACK);
			getGraphics().setFont(new Font(FONT_SIZE + 2));
			String typeText = "Type: " + unite.getResourceType();
			getGraphics().fillText(typeText, xPos + 10, yPos + 25);
			
			// Draw stock value
			getGraphics().setFont(new Font(FONT_SIZE));
			String stockText = "Stock: " + unite.getStockValue();
			getGraphics().fillText(stockText, xPos + 10, yPos + 50);
			
			// Store center position for connections
			positions.put(unite, xPos + UNIT_WIDTH / 2);
		}
		
		return positions;
	}
	
	private Map<MLKAgent, Double> drawAgents(List<MLKAgent> agents) {
		Map<MLKAgent, Double> positions = new HashMap<>();
		double yPos = TOP_MARGIN + UNIT_HEIGHT + MIDDLE_GAP;
		
		for (int i = 0; i < agents.size(); i++) {
			MLKAgent agent = agents.get(i);
			double xPos = 20 + i * (UNIT_WIDTH + 20) + (UNIT_WIDTH - AGENT_SIZE) / 2;
			
			// Draw agent circle
			getGraphics().setFill(RED);
			getGraphics().fillOval(xPos, yPos, AGENT_SIZE, AGENT_SIZE);
			
			// Draw border
			getGraphics().setStroke(BLACK);
			getGraphics().setLineWidth(2);
			getGraphics().strokeOval(xPos, yPos, AGENT_SIZE, AGENT_SIZE);
			
			// Store center position for connections
			positions.put(agent, xPos + AGENT_SIZE / 2);
		}
		
		return positions;
	}
	
	private void drawConnections(Map<MLKAgent, Double> agentPositions, 
	                            Map<ProductionUnit, Double> unitPositions,
	                            Map<MLKAgent, Action> lastActions) {
		double agentY = TOP_MARGIN + UNIT_HEIGHT + MIDDLE_GAP + AGENT_SIZE / 2;
		double unitY = TOP_MARGIN + UNIT_HEIGHT;
		
		for (Map.Entry<MLKAgent, Action> entry : lastActions.entrySet()) {
			MLKAgent agent = entry.getKey();
			Action action = entry.getValue();
			
			if (action instanceof ActionRequestResource arr) {
				ProductionUnit requestedUnit = arr.getUniteProduction();
				
				Double agentX = agentPositions.get(agent);
				Double unitX = unitPositions.get(requestedUnit);
				
				if (agentX != null && unitX != null) {
					getGraphics().setStroke(DARKGRAY);
					getGraphics().setLineWidth(1.5);
					getGraphics().strokeLine(agentX, agentY, unitX, unitY);
				}
			}
		}
	}
}
