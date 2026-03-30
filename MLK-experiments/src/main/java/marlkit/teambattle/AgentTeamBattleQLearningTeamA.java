package marlkit.teambattle;

/**
 * Q-learning TeamBattle agent configured for team A shared-experience role.
 */
public class AgentTeamBattleQLearningTeamA extends AgentTeamBattleQLearning {

	public static final String SHARED_EXPERIENCE_TEAM_ROLE = "AgentTeamBattleA";

	/**
	 * Requests a team-specific role for shared-experience centralized training.
	 */
	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), SHARED_EXPERIENCE_TEAM_ROLE);
	}
}
