package simulation;

import util.criteria.Criterion;

/**
 * Defines scheduling criteria used by MARLKIT schedulers.
 */
public interface SchedulerCriteria {

	/**
	 * Returns the criterion for ending the current episode.
	 *
	 * @return episode end criterion
	 */
	Criterion getCriteriaEndEpisode();

	/**
	 * Returns the criterion for enabling display mode.
	 *
	 * @return display start criterion
	 */
	Criterion getCriteriaStartDisplay();

	/**
	 * Returns the criterion for disabling display mode.
	 *
	 * @return display end criterion
	 */
	Criterion getCriteriaEndDisplay();

	/**
	 * Returns the criterion for ending the simulation.
	 *
	 * @return simulation end criterion
	 */
	Criterion getCriteriaEndSimulation();

	
	public int getPauseDisplayValue();
}
