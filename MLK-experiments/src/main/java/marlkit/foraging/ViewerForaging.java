package marlkit.foraging;

import java.util.List;

import environment.state.State2DGridInt;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

/**
 * Viewer for the Foraging environment.
 * Displays the grid, agents (in red) and food (in green).
 */
public class ViewerForaging extends Viewer2D {
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
	public EnvForaging getEnvironment() {
		return super.getEnvironment();
	}

	@Override
	public void render() {
		super.render();
		List<Agent> envs = stateProbe.getAgents();
		for (Agent env : envs) {
			State2DGridInt s = stateProbe.getPropertyValue(env);
			
			for (int i = 0; i < s.getWidth(); i++) {
				for (int j = 0; j < s.getHeight(); j++) {
					int foodCount = s.getValue(i, j);
					if (foodCount >= 1) {
						getGraphics().setFill(GREEN);
						getGraphics().fillRect(i * CELLSIZE, j * CELLSIZE, CELLSIZE, CELLSIZE);
						
						if (foodCount > 1) {
							getGraphics().setFill(BLACK);
							getGraphics().fillText(String.valueOf(foodCount), 
									i * CELLSIZE + CELLSIZE/2 - 5, 
									j * CELLSIZE + CELLSIZE/2 + 5);
						}
					}
				}
			}
			
			for (Pair<Integer, Integer> pos : s.getAgentsPositions().values()) {
				getGraphics().setFill(RED);
				getGraphics().fillOval(pos.getFirst() * CELLSIZE, pos.getSecond() * CELLSIZE, AGENTSIZE, AGENTSIZE);
			}
		}
		
		for (int i = 0; i <= getEnvironment().getWidth(); i++) {
			getGraphics().setStroke(BLACK);
			getGraphics().strokeLine(i * CELLSIZE, 0, i * CELLSIZE, getEnvironment().getHeight() * CELLSIZE);
		}
		for (int j = 0; j <= getEnvironment().getHeight(); j++) {
			getGraphics().setStroke(BLACK);
			getGraphics().strokeLine(0, j * CELLSIZE, getEnvironment().getWidth() * CELLSIZE, j * CELLSIZE);
		}
	}
}
