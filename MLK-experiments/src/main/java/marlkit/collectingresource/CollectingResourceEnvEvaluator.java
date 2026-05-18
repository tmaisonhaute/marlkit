package marlkit.collectingresource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import evaluation.Measure;
import evaluation.SystemEvaluator;
import reward.ReactionEvent;


public class CollectingResourceEnvEvaluator implements SystemEvaluator {

	protected Measure totalRewardMeasure;
	protected Measure fairnessMeasure;
	protected Measure totalResourcesMeasure;
	
	protected double totalRewardValue;
	protected double totalResourcesAcquired;
	protected Map<MLKAgent, Double> agentCumulativeRewards;
	
	public CollectingResourceEnvEvaluator() {
		this.totalRewardMeasure = new Measure("TotalReward", 0.0);
		this.fairnessMeasure = new Measure("LowestAgentReward", 0.0);
		this.totalResourcesMeasure = new Measure("NumberOfResources", 0.0);
		agentCumulativeRewards = new HashMap<>();
	}
	
	@Override
	public void evaluate(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
		for (MLKAgent agent : reactionEvents.keySet()) {
			List<ReactionEvent> events = reactionEvents.get(agent);
			for (ReactionEvent event : events) {
				double eventReward = event.toReward().getValue();
				totalRewardValue += eventReward;
				agentCumulativeRewards.put(agent, agentCumulativeRewards.getOrDefault(agent, 0.0) + eventReward);
				if (event instanceof CollectResourceEvent2D) {
					totalResourcesAcquired ++;
                }
			}
		}
	}
	
	private double computeLowestAgentReward() {
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
		this.totalRewardMeasure.setValue(totalRewardValue);
		this.fairnessMeasure.setValue(computeLowestAgentReward());
		this.totalResourcesMeasure.setValue(totalResourcesAcquired);
	}

	@Override
	public List<Measure> getEpisodeMeasures() {
		return List.of(totalRewardMeasure, fairnessMeasure, totalResourcesMeasure);
	}

	@Override
	public List<String> getMeasureNames() {
		return List.of(totalRewardMeasure.toString(), fairnessMeasure.toString(), totalResourcesMeasure.toString());
	}
	
	@Override
	public void reset() {
		this.totalRewardValue = 0;
		this.agentCumulativeRewards.clear();
		this.totalResourcesAcquired = 0;
	}

}
