package marlkit.pushtheblock;

import simulation.MLKScheduler;
import simulation.SchedulerCriteria;
import util.criteria.AlwaysMet;
import util.criteria.Criteria;
import util.criteria.Criterion;
import util.criteria.ReachTimeCriterion;

public class SchedulerPTBNoPause extends MLKScheduler {
	
	public static final int EPISODE_DURATION = 100;
	public static final int MAXIMUM_EPISODE_COUNT = 10_000;

	public SchedulerPTBNoPause() {
		setCriteriaModule(new SchedulerPTBCriteria());
	}

	class SchedulerPTBCriteria implements SchedulerCriteria {

		protected Criterion criteriaEndEpisode;
		protected Criterion criteriaEndSimulation;
		protected Criterion alwaysTrue;
		protected Criterion alwaysFalse;
		
		public SchedulerPTBCriteria() {
			criteriaEndEpisode = new ReachTimeCriterion(EPISODE_DURATION);
			criteriaEndSimulation = new ReachTimeCriterion(MAXIMUM_EPISODE_COUNT);
			alwaysTrue = new AlwaysMet();
			alwaysFalse = Criteria.not(new AlwaysMet());
		}

		@Override
		public Criterion getCriteriaEndEpisode() {
			return criteriaEndEpisode;
		}

		@Override
		public Criterion getCriteriaStartDisplay() {
			return alwaysFalse;
		}

		@Override
		public Criterion getCriteriaEndDisplay() {
			return alwaysTrue;
		}

		@Override
		public Criterion getCriteriaEndSimulation() {
			return criteriaEndSimulation;
		}

		@Override
		public Criterion getCriteriaEvaluation() {
			return alwaysTrue;
		}

		@Override
		public int getPauseDisplayValue() {
			return 0;
		}
	}

}