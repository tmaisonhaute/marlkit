package marlkit.gooryield;

import static javafx.scene.paint.Color.BLACK;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import marlkit.gooryield.environment.EnvGoOrYield;

import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;
import simulation.MLKViewer;

public class ViewerGoOrYield extends MLKViewer {

	private PropertyProbe<long[]> outcomeCountsProbe;

	private static final double CANVAS_WIDTH = 520;
	private static final double CANVAS_HEIGHT = 380;

	private static final double GRID_X = 160;
	private static final double GRID_Y = 80;
	private static final double CELL_W = 150;
	private static final double CELL_H = 120;

	@Override
	protected void onActivation() {
		super.onActivation();
		outcomeCountsProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "outcomeCounts");
		addProbe(outcomeCountsProbe);

		getGUI().getCanvas().setWidth(CANVAS_WIDTH);
		getGUI().getCanvas().setHeight(CANVAS_HEIGHT);
	}

	@Override
	public void render() {
		super.render();

		if (outcomeCountsProbe.getAgents().isEmpty()) {
			return;
		}

		Agent env = outcomeCountsProbe.getAgents().get(0);
		long[] counts = outcomeCountsProbe.getPropertyValue(env);
		if (counts == null || counts.length < 4) {
			return;
		}

		long total = 0;
		for (int i = 0; i < 4; i++) {
			total += counts[i];
		}

		drawGrid();
		drawLabels();
		drawProportions(counts, total);
	}

	private void drawGrid() {
		getGraphics().setStroke(BLACK);
		getGraphics().strokeRect(GRID_X, GRID_Y, 2 * CELL_W, 2 * CELL_H);
		getGraphics().strokeLine(GRID_X + CELL_W, GRID_Y, GRID_X + CELL_W, GRID_Y + 2 * CELL_H);
		getGraphics().strokeLine(GRID_X, GRID_Y + CELL_H, GRID_X + 2 * CELL_W, GRID_Y + CELL_H);
	}

	private void drawLabels() {
		getGraphics().setFill(BLACK);
		getGraphics().fillText("Agent 1", GRID_X + CELL_W - 20, GRID_Y - 35);
		getGraphics().fillText("Yield", GRID_X + 45, GRID_Y - 10);
		getGraphics().fillText("Go", GRID_X + CELL_W + 60, GRID_Y - 10);

		getGraphics().fillText("Agent 0", GRID_X - 120, GRID_Y + CELL_H - 5);
		getGraphics().fillText("Yield", GRID_X - 55, GRID_Y + 60);
		getGraphics().fillText("Go", GRID_X - 40, GRID_Y + CELL_H + 60);
	}

	private void drawProportions(long[] counts, long total) {
		drawCellProportion(0, 0, counts[EnvGoOrYield.OUTCOME_YIELD_YIELD], total);
		drawCellProportion(1, 0, counts[EnvGoOrYield.OUTCOME_YIELD_GO], total);
		drawCellProportion(0, 1, counts[EnvGoOrYield.OUTCOME_GO_YIELD], total);
		drawCellProportion(1, 1, counts[EnvGoOrYield.OUTCOME_GO_GO], total);
	}

	private void drawCellProportion(int col, int row, long count, long total) {
		double proportion = total == 0 ? 0.0 : ((double) count) / ((double) total);
		String text = String.format("%.3f", proportion);

		double x = GRID_X + (col * CELL_W) + (CELL_W / 2) - 20;
		double y = GRID_Y + (row * CELL_H) + (CELL_H / 2);
		getGraphics().setFill(BLACK);
		getGraphics().fillText(text, x, y);
	}
}
