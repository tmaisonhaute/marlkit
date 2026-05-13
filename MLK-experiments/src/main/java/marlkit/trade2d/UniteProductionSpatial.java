package marlkit.trade2d;

import java.util.random.RandomGenerator;

import marlkit.trade.ResourceType;
import marlkit.trade.UniteProduction;
import util.MapProba;
import util.Position;

/**
 * Spatial production unit with a continuous position.
 */
public class UniteProductionSpatial extends UniteProduction {
	private Position position;

	public UniteProductionSpatial(ResourceType type, MapProba<Integer> probabilityProduction,
			int initialStock, int maxStock, RandomGenerator prng, Position position) {
		super(type, probabilityProduction, initialStock, maxStock, prng);
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
}
