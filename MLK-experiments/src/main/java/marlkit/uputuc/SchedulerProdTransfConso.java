package marlkit.uputuc;

import simulation.MLKScheduler;
import util.criteria.AlwaysMet;
import util.criteria.Criteria;
import util.criteria.Criterion;
import util.criteria.ReachTimeCriterion;

public class SchedulerProdTransfConso extends MLKScheduler {

	public static final int EPISODE_DURATION = 100;
	public static final int MAXIMUM_EPISODE_COUNT = 1_000;

	private Criterion criteriaEndEpisode = new ReachTimeCriterion(EPISODE_DURATION);
	private Criterion criteriaStartDisplay = new AlwaysMet();
	private Criterion criteriaEndDisplay = Criteria.not(new AlwaysMet());
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
	public Criterion getCriteriaEndSimulation() {
		return criteriaEndSimulation;
	}

}
