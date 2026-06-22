package marlkit.preyhunter.environment.events;

import reward.ReactionEvent;
import reward.Reward;
import reward.RewardStandard;

/**
 * Penalty event based on the distance between a hunter and a prey.
 */
public class HunterPreyDistanceEvent extends ReactionEvent {

    private static final double REWARD_DISTANCE_PENALTY_PREY_HUNTER = -2.0;

    private final double distance;

    public HunterPreyDistanceEvent(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public Reward toReward() {
        return new RewardStandard(distance * REWARD_DISTANCE_PENALTY_PREY_HUNTER);
    }
}