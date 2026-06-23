package marlkit.preyhunter.environment.events;

import reward.ReactionEvent;
import reward.Reward;
import reward.RewardStandard;

/**
 * Penalty event based on the distance between a hunter and a prey.
 */
public class HunterPreyDistanceEvent extends ReactionEvent {

    private static final double REWARD_DISTANCE_PENALTY_PREY_HUNTER = -0.5;
    private static final double REWARD_MAXIMUM_BONUS_CLOSE = 2;
    
    private static final double RANGE_START_PENALTY = 2.0;

    private final double distance;

    public HunterPreyDistanceEvent(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public Reward toReward() {
    	if (distance < RANGE_START_PENALTY) {
    		return new RewardStandard((1 - distance/RANGE_START_PENALTY) * REWARD_MAXIMUM_BONUS_CLOSE);
    	}
        return new RewardStandard((distance - RANGE_START_PENALTY) * REWARD_DISTANCE_PENALTY_PREY_HUNTER);
    }
}