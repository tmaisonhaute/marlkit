package marlkit.preyVsHunter;

import simulation.MLKScheduler;
import util.criteria.Criteria;
import util.criteria.Criterion;
import util.criteria.ModuloTimeCriterion;
import util.criteria.ReachTimeCriterion;

public class SchedulerPVH extends MLKScheduler {

    public static final int EPISODE_DURATION = 100;
    public static final int MINIMUM_STEP_BEFORE_VIEW = 100000000;
    public static final int VIEWER_UPDATE_INTERVAL = 1000000;
    public static final int PAUSE_DISPLAY_VALUE = 50;

    private Criterion criteriaEndEpisode = new ReachTimeCriterion(EPISODE_DURATION);
    private Criterion criteriaStartDisplay = Criteria.and(new ModuloTimeCriterion(VIEWER_UPDATE_INTERVAL), new ReachTimeCriterion(MINIMUM_STEP_BEFORE_VIEW));
    private Criterion criteriaEndDisplay = Criteria.and(new ModuloTimeCriterion(VIEWER_UPDATE_INTERVAL, EPISODE_DURATION), new ReachTimeCriterion(MINIMUM_STEP_BEFORE_VIEW));

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
}
