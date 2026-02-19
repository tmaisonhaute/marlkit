package marlkit.preyVsHunter;

import simulation.MLKScheduler;
import util.criteria.Criteria;
import util.criteria.Criterion;
import util.criteria.ModuloTimeCriterion;
import util.criteria.ReachTimeCriterion;

public class SchedulerPVH extends MLKScheduler {

    public static final int EPISODE_DURATION = 100;
    public static final int MINIMUM_STEP_BEFORE_VIEW = 1000;
    public static final int VIEWER_UPDATE_INTERVAL = 100;
	public static final int DISPLAYED_EPISODES = 1;
    public static final int PAUSE_DISPLAY_VALUE = 50;
	public static final int MAXIMUM_EPISODE_COUNT = 10_000;

    private Criterion criteriaEndEpisode = new ReachTimeCriterion(EPISODE_DURATION);
    private Criterion criteriaStartDisplay = Criteria.and(new ModuloTimeCriterion(VIEWER_UPDATE_INTERVAL), 
    		new ReachTimeCriterion(MINIMUM_STEP_BEFORE_VIEW));
    private Criterion criteriaEndDisplay = Criteria.and(new ModuloTimeCriterion(VIEWER_UPDATE_INTERVAL, DISPLAYED_EPISODES), 
    		new ReachTimeCriterion(MINIMUM_STEP_BEFORE_VIEW));
    private Criterion criteriaEndSimulation = new ReachTimeCriterion(MAXIMUM_EPISODE_COUNT);
    
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
    public int getPauseDisplayValue() {
        return PAUSE_DISPLAY_VALUE; // Enable display by default
    }

	@Override
	public Criterion getCriteriaEndSimulation() {
		return criteriaEndSimulation;
	}
}
