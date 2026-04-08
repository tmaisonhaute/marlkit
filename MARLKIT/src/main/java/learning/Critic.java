package learning;

import agent.MLKAgent;

public interface Critic {

	public void init(MLKAgent agent);
	
	/**
	 * Enriches the original experience with critic-specific information.
	 * @param originalExperience
	 * @param enrichedExperience
	 */
	public void enrichExperience(Experience originalExperience, Experience enrichedExperience);
	
	public void resetEnrichedExperiences();
	
	public Experience getEnrichedExperience(Experience originalExperience);
	
	public void removeEnrichedExperience(Experience originalExperience);
	public void clearEnrichedExperiences();
}
