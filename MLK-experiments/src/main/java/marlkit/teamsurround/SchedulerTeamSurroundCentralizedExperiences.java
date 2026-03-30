package marlkit.teamsurround;

import centralizedtraining.CollectiveExperienceActivator;
import madkit.kernel.Activator;

/**
 * TeamSurround scheduler with centralized shared experiences per team.
 */
public class SchedulerTeamSurroundCentralizedExperiences extends SchedulerTeamSurround {

	private Activator team1CollectiveExperienceActivator;
	private Activator team2CollectiveExperienceActivator;

	@Override
	protected void onActivation() {
		super.onActivation();
		team1CollectiveExperienceActivator = new CollectiveExperienceActivator(getModelGroup(), AgentTeam1.SHARED_EXPERIENCE_TEAM_ROLE);
		team2CollectiveExperienceActivator = new CollectiveExperienceActivator(getModelGroup(), AgentTeam2.SHARED_EXPERIENCE_TEAM_ROLE);
		addActivator(team1CollectiveExperienceActivator);
		addActivator(team2CollectiveExperienceActivator);
		
	}

	@Override
	protected void agentsCollectExperience() {
		team1CollectiveExperienceActivator.execute();
		team2CollectiveExperienceActivator.execute();
	}
	

}