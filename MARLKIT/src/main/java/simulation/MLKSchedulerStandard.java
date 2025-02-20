package simulation;

import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.logging.Level;

import madkit.kernel.Activator;
import madkit.simulation.SimuOrganization;
import madkit.simulation.scheduler.MethodActivator;
import madkit.simulation.scheduler.TickBasedScheduler;

public class MLKSchedulerStandard extends TickBasedScheduler {
	private Activator agentSendInfo;
	private Activator agentUpdatePolicy;
	private Activator agentEndEpisode;

	private Activator step;

	private MethodActivator viewers;
	
	private int counter = 0;

	@Override
	protected void onActivation() {
		getLogger().setLevel(Level.INFO);
		super.onActivation();
		final String roleAgent = "mlkagent";
		agentSendInfo = new MethodActivator(getModelGroup(), roleAgent, "sendInfo");
		addActivator(agentSendInfo);
		agentEndEpisode = new MethodActivator(getModelGroup(), roleAgent, "endEpisode");
		addActivator(agentEndEpisode);
		agentUpdatePolicy = new MethodActivator(getModelGroup(), roleAgent, "updatePolicy");
		addActivator(agentUpdatePolicy);
		
		
		step = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "step");
		addActivator(step);
		viewers = new MethodActivator(getEngineGroup(), SimuOrganization.VIEWER_ROLE, "display");
		addActivator(viewers);
	}

	public void agentShareInformation() {
		agentSendInfo.execute();
	}

	@Override
	public void doSimulationStep() {
		super.doSimulationStep();
		step.execute();
		agentUpdatePolicy.execute(counter);
		if (counter % 100 == 0) {
			agentEndEpisode.execute();
		}
		if (counter % 100000 == 0 && counter >= 1000000) {
			setPause(50);
		}
		else if(counter %100000 == 100) {
			setPause(0);
		}
		counter++;
		viewers.execute();
	}

}
