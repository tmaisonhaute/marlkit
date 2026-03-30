package centralizedtraining;

import simulation.MLKScheduler;

public abstract class SchedulerCentralizedExperiences extends MLKScheduler {
	public static final String SHARED_EXPERIENCE_AGENT_ROLE = "AgentSharingExperience";
	
	private CollectiveExperienceActivator collectiveExperienceActivator;

	@Override
	protected void onActivation() {
		super.onActivation();
		collectiveExperienceActivator = new CollectiveExperienceActivator(getModelGroup(), SHARED_EXPERIENCE_AGENT_ROLE);
		addActivator(collectiveExperienceActivator);
	}

	@Override
	protected void agentsCollectExperience() {
		collectiveExperienceActivator.execute();
	}

}
