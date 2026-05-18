package evaluation;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import reward.ReactionEvent;

public class NoSystemEvaluation implements SystemEvaluator {

	@Override
	public void evaluate(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
		// No evaluation logic
	}

	@Override
	public List<Measure> getEpisodeMeasures() {
		return List.of();
	}

	@Override
	public List<String> getMeasureNames() {
		return List.of();
	}

	@Override
	public void reset() {
		// No state to reset
	}
	

}
