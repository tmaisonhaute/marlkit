package simulation;

import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.logging.Level;

import madkit.kernel.Activator;
import madkit.simulation.SimuOrganization;
import madkit.simulation.scheduler.MethodActivator;
import madkit.simulation.scheduler.TickBasedScheduler;

public class MLKSchedulerStandard extends TickBasedScheduler {
	public static final int EPISODE_DURATION = 100;
	public static final int MINIMUM_STEP_BEFORE_VIEW = 1000000;
	public static final int VIEWER_UPDATE_INTERVAL = 100000;
	
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
		if (counter % EPISODE_DURATION == 0) {
			agentEndEpisode.execute();
		}
		if (counter % VIEWER_UPDATE_INTERVAL == 0 && counter >= MINIMUM_STEP_BEFORE_VIEW) {
			setPause(50);
		}
		else if(counter % VIEWER_UPDATE_INTERVAL == EPISODE_DURATION) {
			setPause(0);
		}
		counter++;
		viewers.execute();
	}

}
