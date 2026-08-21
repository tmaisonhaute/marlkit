package simulation;

import util.criteria.AlwaysMet;
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
	
	/**
	 * Returns the criterion for evaluating the performance of the agents.
	 * 
	 * <p> By default, this method returns an instance of {@link AlwaysMet}, indicating that the evaluation criterion is always met. 
	 * Implementations can override this method to provide a specific evaluation criterion based on the simulation's requirements.</p>
	 * 
	 * @return the evaluation criterion
	 */
	Criterion getCriteriaEvaluation();

	
	public int getPauseDisplayValue();
}
