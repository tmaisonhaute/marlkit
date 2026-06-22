package marlkit.preyhunter.environment.events;

import reward.ReactionEvent;
import reward.Reward;
import reward.RewardStandard;

/**
 * Penalty event based on the distance between two hunters.
 * 
 * Penalizes hunters that are too close, but also slightly penalizes hunters
 * that are far apart.
 */
public class HunterHunterDistanceEvent extends ReactionEvent {

    private static final double MAXIMUM_PENALTY_REWARD = -10.0;
    private static final double TOO_CLOSE_DISTANCE = 3.0;

    private final double distance;

    public HunterHunterDistanceEvent(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public Reward toReward() {
		if (distance <= TOO_CLOSE_DISTANCE && TOO_CLOSE_DISTANCE > 0) {
			return new RewardStandard((1 - distance/TOO_CLOSE_DISTANCE) * MAXIMUM_PENALTY_REWARD);
		}
		return new RewardStandard(0);
    }
}