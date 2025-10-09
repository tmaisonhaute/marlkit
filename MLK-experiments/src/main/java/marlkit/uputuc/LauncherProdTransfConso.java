package marlkit.uputuc;

import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerProdTransfConso.class, environment = EnvPTC.class, model = MLKModel.class, viewers = {
		ViewerProdTransfConso.class })
public class LauncherProdTransfConso extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		// TODO Auto-generated method stub

	}

}
