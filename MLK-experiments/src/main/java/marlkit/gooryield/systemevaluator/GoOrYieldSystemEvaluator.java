package marlkit.gooryield.systemevaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import evaluation.Measure;
import evaluation.SystemEvaluator;
import marlkit.gooryield.environment.events.MatrixRewardEvent;
import reward.ReactionEvent;

public class GoOrYieldSystemEvaluator implements SystemEvaluator {

    protected Measure yieldGoProportionMeasure;
    protected Measure goGoProportionMeasure;
    protected Measure yieldYieldProportionMeasure;
    protected Measure totalRewardMeasure;
    protected Measure selfGoProportionMeasure;
    protected Measure opponentGoProportionMeasure;

    protected int yieldGoCount;
    protected int goGoCount;
    protected int yieldYieldCount;
    protected int totalStepsCount;
    protected List<Boolean> selfGoList;
    protected List<Boolean> opponentGoList;

    protected double totalRewardValue;

    public GoOrYieldSystemEvaluator() {
        this.yieldGoProportionMeasure = new Measure("YieldGoProportion", 0.0);
        this.goGoProportionMeasure = new Measure("GoGoProportion", 0.0);
        this.yieldYieldProportionMeasure = new Measure("YieldYieldProportion", 0.0);
        this.totalRewardMeasure = new Measure("TotalReward", 0.0);
        this.selfGoProportionMeasure = new Measure("SelfGoProportion", 0.0);
        this.opponentGoProportionMeasure = new Measure("OpponentGoProportion", 0.0);
        selfGoList = new ArrayList<>();
        opponentGoList = new ArrayList<>();
        
    }

    /**
     * Evaluate the reaction events produced at the current step.
     *
     * @param reactionEvents reaction events indexed by agent.
     */
    @Override
    public void evaluate(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
        if (isEmptyEvaluation(reactionEvents)) {
            return;
        }

        updateTotalReward(reactionEvents);
        updateOutcomeCounts(reactionEvents);
    }

    /**
     * Return whether the provided evaluation input contains no exploitable data.
     *
     * @param reactionEvents reaction events indexed by agent.
     * @return true if the input is null or empty.
     */
    protected boolean isEmptyEvaluation(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
        return reactionEvents == null || reactionEvents.isEmpty();
    }

    /**
     * Accumulate the total reward produced by all reaction events of the current step.
     *
     * @param reactionEvents reaction events indexed by agent.
     */
    protected void updateTotalReward(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
        for (List<ReactionEvent> events : reactionEvents.values()) {
            for (ReactionEvent event : events) {
                totalRewardValue += event.toReward().getValue();
            }
        }
    }

    /**
     * Update outcome counters from one representative matrix reward event of the current step.
     *
     * @param reactionEvents reaction events indexed by agent.
     */
    protected void updateOutcomeCounts(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
        MatrixRewardEvent representativeEvent = findRepresentativeMatrixRewardEvent(reactionEvents);
        if (representativeEvent == null) {
            return;
        }

        totalStepsCount++;

        if (representativeEvent.isGoGo()) {
            goGoCount++;
        }
        else if (representativeEvent.isYieldYield()) {
            yieldYieldCount++;
        }
        else if (representativeEvent.isGoYield()) {
            yieldGoCount++;
        }
		
        selfGoList.add(representativeEvent.isSelfGo());
		opponentGoList.add(representativeEvent.isOtherGo());
		if (opponentGoList.size() > 500) {
			opponentGoList.remove(0);
		}
		if (selfGoList.size() > 500) {
			selfGoList.remove(0);
		}
		
    }

    /**
     * Find one matrix reward event representative of the current step.
     *
     * @param reactionEvents reaction events indexed by agent.
     * @return a matrix reward event if one is found, null otherwise.
     */
    protected MatrixRewardEvent findRepresentativeMatrixRewardEvent(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
        for (List<ReactionEvent> events : reactionEvents.values()) {
            for (ReactionEvent event : events) {
                if (event instanceof MatrixRewardEvent mre) {
                    return mre;
                }
            }
        }
        return null;
    }

    @Override
    public void onEpisodeEnd() {
        double yieldGoProp = totalStepsCount == 0 ? 0.0 : ((double) yieldGoCount) / totalStepsCount;
        double goGoProp = totalStepsCount == 0 ? 0.0 : ((double) goGoCount) / totalStepsCount;
        double yieldYieldProp = totalStepsCount == 0 ? 0.0 : ((double) yieldYieldCount) / totalStepsCount;
        double selfGoProp = selfGoList.isEmpty() ? 0.0 : ((double) selfGoList.stream().filter(go -> go).count()) / selfGoList.size();
        double opponentGoProp = opponentGoList.isEmpty() ? 0.0 : ((double) opponentGoList.stream().filter(go -> go).count()) / opponentGoList.size();

        yieldGoProportionMeasure.setValue(yieldGoProp);
        goGoProportionMeasure.setValue(goGoProp);
        yieldYieldProportionMeasure.setValue(yieldYieldProp);
        totalRewardMeasure.setValue(totalRewardValue);
        selfGoProportionMeasure.setValue(selfGoProp);
        opponentGoProportionMeasure.setValue(opponentGoProp);
    }

    @Override
    public List<Measure> getEpisodeMeasures() {
        return List.of(
                yieldGoProportionMeasure,
                goGoProportionMeasure,
                yieldYieldProportionMeasure,
                totalRewardMeasure,
                selfGoProportionMeasure,
                opponentGoProportionMeasure
        );
    }

    @Override
    public List<String> getMeasureNames() {
        return List.of(
                yieldGoProportionMeasure.toString(),
                goGoProportionMeasure.toString(),
                yieldYieldProportionMeasure.toString(),
                totalRewardMeasure.toString(),
                selfGoProportionMeasure.toString(),
                opponentGoProportionMeasure.toString()
        );
    }

    @Override
    public void reset() {
        totalRewardValue = 0.0;
    }
}
