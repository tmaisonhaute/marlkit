package marlkit.test;

import madkit.gui.UIProperty;
import madkit.kernel.Agent;

public class GUITestAgent extends Agent {

	@UIProperty(category = "yes")
	private int test = 0;

	@Override
	protected void onActivation() {
		setupDefaultGUI();
		for (int i = 0; i < 20; i++) {
			getLogger().info("heddddddllo");
		}
	}

	@Override
	protected void onEnd() {
		getLogger().info("I am doned ");
	}

	public static void main(String[] args) {
		executeThisAgent("--kernelLogLevel", "INFO");
	}

	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}

}
