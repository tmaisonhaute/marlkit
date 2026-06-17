package marlkit.collectingresource.environment.events;

import reward.ReactionEventDefault;

/**
 * Distance-based penalty when requesting a resource.
 */
public class DistancePenaltyEvent extends ReactionEventDefault {
	private final double distance;
	private final double penaltyPerUnit;

	/**
	 * Create a distance penalty event.
	 *
	 * @param distance Distance between agent and unit.
	 * @param penaltyPerUnit Penalty applied per distance unit.
	 */
	public DistancePenaltyEvent(double distance, double penaltyPerUnit) {
		super(-distance * penaltyPerUnit);
		this.distance = distance;
		this.penaltyPerUnit = penaltyPerUnit;
	}

	/**
	 * Return the distance used by this event.
	 *
	 * @return Distance value.
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Return the penalty coefficient.
	 *
	 * @return Penalty per unit of distance.
	 */
	public double getPenaltyPerUnit() {
		return penaltyPerUnit;
	}
}
