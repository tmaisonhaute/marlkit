package marlkit.foraging.systemevaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import evaluation.Measure;
import evaluation.SystemEvaluator;
import reward.ReactionEvent;

public class ForagingEnvEvaluator implements SystemEvaluator {

	protected Measure totalRewardMeasure;
	protected Measure episodeDurationMeasure;
	protected Measure lowestAgentRewardMeasure;

	protected double totalRewardValue;
	protected int episodeDurationValue;
	protected Map<MLKAgent, Double> agentCumulativeRewards;

	public ForagingEnvEvaluator() {
		this.totalRewardMeasure = new Measure("TotalReward", 0.0);
		this.episodeDurationMeasure = new Measure("EpisodeDuration", 0.0);
		this.lowestAgentRewardMeasure = new Measure("LowestAgentReward", 0.0);
		this.agentCumulativeRewards = new HashMap<>();
	}

	@Override
	public void evaluate(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
		episodeDurationValue++;

		for (MLKAgent agent : reactionEvents.keySet()) {
			agentCumulativeRewards.putIfAbsent(agent, 0.0);

			for (ReactionEvent event : reactionEvents.get(agent)) {
				double eventReward = event.toReward().getValue();
				totalRewardValue += eventReward;
				agentCumulativeRewards.put(agent, agentCumulativeRewards.get(agent) + eventReward);
			}
		}
	}

	private double computeLowestAgentReward() {
		if (agentCumulativeRewards.isEmpty()) {
			return 0.0;
		}

		double lowestReward = Double.MAX_VALUE;

		for (double reward : agentCumulativeRewards.values()) {
			if (reward < lowestReward) {
				lowestReward = reward;
			}
		}

		return lowestReward;
	}

	@Override
	public void onEpisodeEnd() {
		totalRewardMeasure.setValue(totalRewardValue);
		episodeDurationMeasure.setValue((double) episodeDurationValue);
		lowestAgentRewardMeasure.setValue(computeLowestAgentReward());
	}

	@Override
	public List<Measure> getEpisodeMeasures() {
		return List.of(totalRewardMeasure, episodeDurationMeasure, lowestAgentRewardMeasure);
	}

	@Override
	public List<String> getMeasureNames() {
		return List.of(totalRewardMeasure.toString(), episodeDurationMeasure.toString(), lowestAgentRewardMeasure.toString());
	}

	@Override
	public void reset() {
		totalRewardValue = 0.0;
		episodeDurationValue = 0;
		agentCumulativeRewards.clear();
	}
}