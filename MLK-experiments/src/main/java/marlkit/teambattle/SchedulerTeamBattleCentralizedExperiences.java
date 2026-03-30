package marlkit.teambattle;

import centralizedtraining.CollectiveExperienceActivator;
import madkit.kernel.Activator;

/**
 * TeamBattle scheduler with centralized shared experiences per team.
 */
public class SchedulerTeamBattleCentralizedExperiences extends SchedulerTeamBattle {

	private Activator teamACollectiveExperienceActivator;
	private Activator teamBCollectiveExperienceActivator;

	@Override
	protected void onActivation() {
		super.onActivation();
		teamACollectiveExperienceActivator = new CollectiveExperienceActivator(getModelGroup(), AgentTeamBattleQLearningTeamA.SHARED_EXPERIENCE_TEAM_ROLE);
		teamBCollectiveExperienceActivator = new CollectiveExperienceActivator(getModelGroup(), AgentTeamBattleQLearningTeamB.SHARED_EXPERIENCE_TEAM_ROLE);
		addActivator(teamACollectiveExperienceActivator);
		addActivator(teamBCollectiveExperienceActivator);
	}

	@Override
	protected void agentsCollectExperience() {
		teamACollectiveExperienceActivator.execute();
		teamBCollectiveExperienceActivator.execute();
	}
}
