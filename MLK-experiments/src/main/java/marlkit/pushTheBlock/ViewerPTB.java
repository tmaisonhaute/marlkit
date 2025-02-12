package marlkit.pushTheBlock;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import madkit.simulation.viewer.Viewer2D;
import util.Pair;

public class ViewerPTB extends Viewer2D {
	PropertyProbe<Map<MLKAgent, Pair<Integer, Integer>>> agentsPositions;
	
	@SuppressWarnings("unchecked")
	@Override
	public EnvPushTheBlock getEnvironment() {
		return super.getEnvironment();
	}

	@Override
	protected void onActivation() {
		super.onActivation();
		agentsPositions = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "getAgentsPositions");
		addProbe(agentsPositions);
	}
	
	@Override
	public void render() {
		getGraphics().setFill(WHITE);
		getGraphics().fillRect(0, 0, getEnvironment().getWidth(), getEnvironment().getHeight());
		getGraphics().setFill(BLACK);
		System.out.println("on est ici");
		
		List<Agent> envs = agentsPositions.getAgents();
		for (Agent env : envs) {
			Map<MLKAgent, Pair<Integer, Integer>> m = agentsPositions.getPropertyValue(env);
			for (MLKAgent a : m.keySet()) {
				Pair<Integer, Integer> pos = m.get(a);
				getGraphics().fillOval(pos.getFirst(), pos.getSecond(), 10, 10);
			}
		}
	}
}
