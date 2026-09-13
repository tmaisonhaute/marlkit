package marlkit.preyhunter.systemevaluator;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import evaluation.Measure;
import evaluation.SystemEvaluator;
import marlkit.preyhunter.environment.events.PreyCatchEvent;
import marlkit.preyhunter.scheduler.SchedulerPVH;
import reward.ReactionEvent;

public class PreyHunterSystemEvaluator implements SystemEvaluator {

    private final Measure totalRewardMeasure;
    private final Measure captureSuccessMeasure;
    private final Measure captureTimeMeasure;

    private final int maxEpisodeTime;

    private double totalReward;
    private boolean captured;
    private int elapsedTime;
    private int captureTime;
    
	public PreyHunterSystemEvaluator() {
		this(SchedulerPVH.EPISODE_DURATION);
	}

    public PreyHunterSystemEvaluator(int maxEpisodeTime) {
        if (maxEpisodeTime <= 0) {
            throw new IllegalArgumentException("maxEpisodeTime must be > 0.");
        }

        this.maxEpisodeTime = maxEpisodeTime;
        this.totalRewardMeasure = new Measure("TotalReward", 0.0);
        this.captureSuccessMeasure = new Measure("CaptureSuccess", 0.0);
        this.captureTimeMeasure = new Measure("CaptureTime", (double) maxEpisodeTime);
        reset();
    }

    @Override
    public void evaluate(Map<MLKAgent, List<ReactionEvent>> reactionEvents) {
        elapsedTime++;

        for (List<ReactionEvent> events : reactionEvents.values()) {
            evaluateEvents(events);
        }
    }

    private void evaluateEvents(List<ReactionEvent> events) {
        for (ReactionEvent event : events) {
            totalReward += event.toReward().getValue();

            if (!captured && event instanceof PreyCatchEvent) {
                captured = true;
                captureTime = elapsedTime;
            }
        }
    }

    @Override
    public void onEpisodeEnd() {
        totalRewardMeasure.setValue(totalReward);
        captureSuccessMeasure.setValue(captured ? 1.0 : 0.0);
        captureTimeMeasure.setValue((double) (captured ? captureTime : maxEpisodeTime));
    }

    @Override
    public List<Measure> getEpisodeMeasures() {
        return List.of(totalRewardMeasure, captureSuccessMeasure, captureTimeMeasure);
    }

    @Override
    public List<String> getMeasureNames() {
        return List.of(totalRewardMeasure.toString(), captureSuccessMeasure.toString(), captureTimeMeasure.toString());
    }

    @Override
    public void reset() {
        totalReward = 0.0;
        captured = false;
        elapsedTime = 0;
        captureTime = maxEpisodeTime;
    }
}
