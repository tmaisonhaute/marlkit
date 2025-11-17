package marlkit.uputuc;

import java.util.ArrayList;
import java.util.List;

import environment.observation.Observation;
import marlkit.uputuc.unite.UniteTransformation;

/**
 * Observation used by a UniteTransformation to request sell offers to other
 */
public class ObservationSell implements Observation {
	private int[] sellerResourceStockVectorized;
	
	private List<Need> buyerNeeds;
	
	public ObservationSell(UniteTransformation uniteSource, List<TradeProposal> tradesNeeds) {
		this.sellerResourceStockVectorized = uniteSource.getResourcesStock().getVectorizedStock();
		this.buyerNeeds = new ArrayList<>();
		for (TradeProposal tp : tradesNeeds) {
			Double distance = uniteSource.distanceTo(tp.getAgentSource());
			Need need = new Need(tp.getResourceQuantify().toOneHotEncoding(), tp.getPriceUnite(), distance);
			this.buyerNeeds.add(need);
		}
	}

	@Override
	public Observation add(Observation other) {
		// TODO Auto-generated method stub
		return null;
	}

}

class Need{
	private int[] resourceVectorized;
	private float pricePerUnit;
	private Double distance;
	
	public Need(int[] resourceVectorized, float pricePerUnit, Double distance) {
		super();
		this.resourceVectorized = resourceVectorized;
		this.pricePerUnit = pricePerUnit;
		this.distance = distance;
	}
	
	public int[] getResourceOneHot() {
		return resourceVectorized;
	}
	public void setResourceOneHot(int[] resourceOneHot) {
		this.resourceVectorized = resourceOneHot;
	}
	public float getPrice() {
		return pricePerUnit;
	}
	public void setPrice(float pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
}
