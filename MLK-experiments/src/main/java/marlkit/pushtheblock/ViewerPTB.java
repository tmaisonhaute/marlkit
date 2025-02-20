package marlkit.pushtheblock;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.YELLOW;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.List;

import environment.state.State2DGridInt;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

/**
 * The Class ViewerPTB.
 */
public class ViewerPTB extends Viewer2D {
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
	public EnvPushTheBlock getEnvironment() {
		return super.getEnvironment();
	}

	@Override
	public void render() {
		super.render();
		List<Agent> envs = stateProbe.getAgents();
		for (Agent env : envs) {
			State2DGridInt s = stateProbe.getPropertyValue(env);
			for (Pair<Integer, Integer> pos : s.getAgentsPositions().values()) {
				getGraphics().setFill(RED);
				getGraphics().fillOval(pos.getFirst() * CELLSIZE, pos.getSecond() * CELLSIZE, AGENTSIZE, AGENTSIZE);
			}

			for (int i = 0; i < s.getWidth(); i++) {
				for (int j = 0; j < s.getHeight(); j++) {
					if (s.getValue(i, j) == 1) {
						getGraphics().setFill(YELLOW);
						getGraphics().fillRect(i * CELLSIZE, j * CELLSIZE, CELLSIZE, CELLSIZE);
					}
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
