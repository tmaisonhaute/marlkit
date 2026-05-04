package marlkit.uputuc;

import java.util.List;

import environment.observation.Observation;
import util.Pair;

/**
 * Observation used by a UniteTransformation to request buy offers from other
 * unites. It contains a list of ResourcesStock and their respective distances
 * to the UniteTransformation.
 */
public class ObservationBuy implements Observation {
	private List<Pair<ResourcesStock, Double>> stocksAndDistances;
	

	@Override
	public Observation add(Observation other) {
		if (!(other instanceof ObservationBuy)) {
	        throw new IllegalArgumentException("Impossible to add a ObservationBuy element with an element which isn't.");
	    }
		ObservationBuy otherOA = (ObservationBuy) other;
		ObservationBuy result = new ObservationBuy();

		List<Pair<ResourcesStock, Double>> totalStocksAndDistances = this.getStocksAndDistances();
		totalStocksAndDistances.addAll(otherOA.getStocksAndDistances());
		
		result.setStocksAndDistances(totalStocksAndDistances);
		return result;
	}

	public List<Pair<ResourcesStock, Double>> getStocksAndDistances() {
		return stocksAndDistances;
	}

	public void setStocksAndDistances(List<Pair<ResourcesStock, Double>> stocksAndDistances) {
		this.stocksAndDistances = stocksAndDistances;
	}

	@Override
	public Observation copy() {
		ObservationBuy copy = new ObservationBuy();
		for (Pair<ResourcesStock, Double> pair : this.stocksAndDistances) {
			copy.getStocksAndDistances().add(new Pair<>(pair.getFirst(), pair.getSecond()));
		}
		return copy;
	}
	

}
