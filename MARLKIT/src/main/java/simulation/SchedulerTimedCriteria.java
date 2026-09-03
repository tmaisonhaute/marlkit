package simulation;

import util.criteria.AlwaysMet;
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
	protected Criterion criteriaEvaluation;

	public void initEpisodeDuration(int episodeDuration) {
		criteriaEndEpisode = new ReachTimeCriterion(episodeDuration);
	}

	public void initStartDisplay(int updateDisplayInterval, int minimumEpisodesBeforeView) {
		criteriaStartDisplay = Criteria.and(new ModuloTimeCriterion(updateDisplayInterval),
				new ReachTimeCriterion(minimumEpisodesBeforeView));
	}

	public void initEndDisplay(int updateInterval, int minimumEpisodesBeforeView, int displayedEpisodes) {
		criteriaEndDisplay = Criteria.and(new ModuloTimeCriterion(updateInterval, displayedEpisodes),
				new ReachTimeCriterion(minimumEpisodesBeforeView));
	}

	public void initEndSimulation(int maximumEpisodeCount) {
		criteriaEndSimulation = new ReachTimeCriterion(maximumEpisodeCount);
	}
	
	public void initEvaluationCriterion(){
		criteriaEvaluation = new AlwaysMet();
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
	public Criterion getCriteriaEvaluation() {
		return criteriaEvaluation;
	}

	@Override
	public int getPauseDisplayValue() {
		return pauseDisplayValue;
	}

}
