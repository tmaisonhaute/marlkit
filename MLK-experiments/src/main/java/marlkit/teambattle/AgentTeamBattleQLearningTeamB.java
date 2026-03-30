package marlkit.teambattle;

/**
 * Q-learning TeamBattle agent configured for team B shared-experience role.
 */
public class AgentTeamBattleQLearningTeamB extends AgentTeamBattleQLearning {

	public static final String SHARED_EXPERIENCE_TEAM_ROLE = "AgentTeamBattleB";

	/**
	 * Requests a team-specific role for shared-experience centralized training.
	 */
	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), SHARED_EXPERIENCE_TEAM_ROLE);
	}
}
