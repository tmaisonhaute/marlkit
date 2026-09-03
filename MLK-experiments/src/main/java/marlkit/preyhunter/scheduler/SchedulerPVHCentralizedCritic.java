package marlkit.preyhunter.scheduler;

import centralizedtraining.CentralizedCriticTrainingExecutionStrategy;

public class SchedulerPVHCentralizedCritic extends SchedulerPVH {

	public SchedulerPVHCentralizedCritic() {
		setCriteriaModule(new SchedulerPVHCriteria());
		setTrainingExecutionStrategy(new CentralizedCriticTrainingExecutionStrategy());
	}

}
