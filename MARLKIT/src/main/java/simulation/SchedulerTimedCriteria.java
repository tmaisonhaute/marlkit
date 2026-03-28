package simulation;

import util.criteria.Criteria;
import util.criteria.Criterion;
import util.criteria.ModuloTimeCriterion;
import util.criteria.ReachTimeCriterion;

public abstract class SchedulerTimedCriteria implements SchedulerCriteria {

	public int pauseDisplayValue = 100;

	private Criterion criteriaEndEpisode;
	private Criterion criteriaStartDisplay;
	private Criterion criteriaEndDisplay;
	private Criterion criteriaEndSimulation;

	public void initEpisodeDuration(int episodeDuration) {
		criteriaEndEpisode = new ReachTimeCriterion(episodeDuration);
	}

	public void initStartDisplay(int updateInterval, int minimumStepBeforeView) {
		criteriaStartDisplay = Criteria.and(new ModuloTimeCriterion(updateInterval),
				new ReachTimeCriterion(minimumStepBeforeView));
	}

	public void initEndDisplay(int updateInterval, int minimumStepBeforeView, int displayedEpisodes) {
		criteriaEndDisplay = Criteria.and(new ModuloTimeCriterion(updateInterval, displayedEpisodes),
				new ReachTimeCriterion(minimumStepBeforeView));
	}

	public void initEndSimulation(int maximumEpisodeCount) {
		criteriaEndSimulation = new ReachTimeCriterion(maximumEpisodeCount);
	}

	public void setCriteriaEndEpisode(Criterion criteriaEndEpisode) {
		this.criteriaEndEpisode = criteriaEndEpisode;
	}
	public void setCriteriaStartDisplay(Criterion criteriaStartDisplay) {
		this.criteriaStartDisplay = criteriaStartDisplay;
	}
	public void setCriteriaEndDisplay(Criterion criteriaEndDisplay) {
		this.criteriaEndDisplay = criteriaEndDisplay;
	}
	public void setCriteriaEndSimulation(Criterion criteriaEndSimulation) {
		this.criteriaEndSimulation = criteriaEndSimulation;
	}

	public void setPauseDisplayValue(int pauseDisplayValue) {
		this.pauseDisplayValue = pauseDisplayValue;
	}
	
	@Override
	public Criterion getCriteriaEndEpisode() {
		return criteriaEndEpisode;
	}

	@Override
	public Criterion getCriteriaStartDisplay() {
		return criteriaStartDisplay;
	}

	@Override
	public Criterion getCriteriaEndDisplay() {
		return criteriaEndDisplay;
	}

	@Override
	public Criterion getCriteriaEndSimulation() {
		return criteriaEndSimulation;
	}

	@Override
	public int getPauseDisplayValue() {
		return pauseDisplayValue;
	}

}
