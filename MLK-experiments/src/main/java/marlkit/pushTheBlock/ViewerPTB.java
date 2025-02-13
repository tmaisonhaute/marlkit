package marlkit.pushTheBlock;

import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.YELLOW;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.List;

import environment.state.State2DGridInt;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

public class ViewerPTB extends Viewer2D {
	PropertyProbe<State2DGridInt> stateProbe;
	private static final double cellSize = 50;
	private static final double agentSize = 50;
	
	@Override
	protected void onActivation() {
		super.onActivation();
		stateProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "state");
		addProbe(stateProbe);
		getGUI().getCanvas().setWidth(getEnvironment().getWidth() * cellSize);
		getGUI().getCanvas().setHeight(getEnvironment().getHeight() * cellSize);
		getGUI().setSynchroPainting(false);
	}
	
	@Override
	public EnvPushTheBlock getEnvironment() {
		// TODO Auto-generated method stub
		return super.getEnvironment();
	}
	
	@Override
	public void render() {
		super.render();
		List<Agent> envs = stateProbe.getAgents();
		for (Agent env : envs) {
			State2DGridInt s = stateProbe.getPropertyValue(env);
			for (Pair<Integer, Integer> pos : s.getAgentsPositions().values()){
				getGraphics().setFill(RED);
				getGraphics().fillOval(pos.getFirst()*cellSize, pos.getSecond()*cellSize, agentSize, agentSize);
			}
			
			for (int i = 0; i < s.getWidth() ; i++) {
				for (int j = 0; j < s.getHeight(); j++) {
					if (s.getValue(i, j) == 1) {
						getGraphics().setFill(YELLOW);
						getGraphics().fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
					}
				}
			}
		}
	}
}
