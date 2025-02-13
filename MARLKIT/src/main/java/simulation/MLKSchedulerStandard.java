package simulation;

import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.logging.Level;

import madkit.kernel.Activator;
import madkit.simulation.SimuOrganization;
import madkit.simulation.scheduler.MethodActivator;
import madkit.simulation.scheduler.TickBasedScheduler;

public class MLKSchedulerStandard extends TickBasedScheduler {
	private Activator agentSendInfo;
	
	private Activator step;
	
	private MethodActivator viewers;
	
	
	@Override
	protected void onActivation() {
		getLogger().setLevel(Level.INFO);
		super.onActivation();
		
		setPause(50);
		
		agentSendInfo = new MethodActivator(getModelGroup(), "mlkagent", "sendInfo");
		addActivator(agentSendInfo);
		
		step = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "step");
		addActivator(step);
		viewers = new MethodActivator(getEngineGroup(), SimuOrganization.VIEWER_ROLE, "display");
		addActivator(viewers);
	}
	
	public void agentShareInformation(){
		agentSendInfo.execute();
	}
	
	@Override
	public void doSimulationStep() {
		super.doSimulationStep();
		step.execute();
	}
	
}
