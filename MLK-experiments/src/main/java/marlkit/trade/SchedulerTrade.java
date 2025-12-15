package marlkit.trade;

import simulation.MLKScheduler;
import util.criteria.Criteria;
import util.criteria.Criterion;
import util.criteria.ModuloTimeCriterion;
import util.criteria.ReachTimeCriterion;

public class SchedulerTrade extends MLKScheduler {
	
	public static final int EPISODE_DURATION = 100;
	public static final int MINIMUM_STEP_BEFORE_VIEW = 10;
	public static final int UPDATE_INTERVAL = 100;
	public static final int PAUSE_DISPLAY_VALUE = 50;
	public static final int MAXIMUM_EPISODE_COUNT = 1_0000;

	private final Criterion criteriaEndEpisode = new ReachTimeCriterion(EPISODE_DURATION);
	private final Criterion criteriaStartDisplay = Criteria.and(new ModuloTimeCriterion(UPDATE_INTERVAL), new ReachTimeCriterion(MINIMUM_STEP_BEFORE_VIEW));
	private final Criterion criteriaEndDisplay = Criteria.and(new ModuloTimeCriterion(UPDATE_INTERVAL, EPISODE_DURATION), new ReachTimeCriterion(MINIMUM_STEP_BEFORE_VIEW));
	private final Criterion criteriaEndSimulation = new ReachTimeCriterion(MAXIMUM_EPISODE_COUNT);

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
		return PAUSE_DISPLAY_VALUE;
	}

}
