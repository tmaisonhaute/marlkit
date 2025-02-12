package simulation;

import java.time.Instant;

import madkit.kernel.Activator;
import madkit.simulation.scheduler.TickBasedScheduler;

public class MLKSchedulerStandard extends TickBasedScheduler {
	private Activator agentSendInfo;
	private Activator envGetInfo;
	
	private Activator step;
	
	private int counter;
	private Instant begin;
	
	@Override
	protected void onActivation() {
		throw new RuntimeException("on est dans le mdk scheduler");
//		getLogger().setLevel(Level.FINER);
//		super.onActivation();
//		
//		agentSendInfo = new MethodActivator(getModelGroup(), "mlkagent", "sendInfo");
//		addActivator(agentSendInfo);
//		envGetInfo = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "receiveAgentsInfo");
//		addActivator(envGetInfo);
//		
//		step = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "step");
//		addActivator(step);
	}
	
	public void agentShareInformation(){
		agentSendInfo.execute();
		envGetInfo.execute();
	}
	
	@Override
	public void doSimulationStep() {
		counter++;

		step.execute();
//		logSpeed();
	}
	
//	private void logSpeed() {
//		if (begin == null) {
//			begin = Instant.now();
//		} 
//		if(counter%100 == 0) {
//			getLogger().info(() -> ""+Duration.between(begin,Instant.now()).toMillis());
//			begin = null;
//		}
//	}
}
