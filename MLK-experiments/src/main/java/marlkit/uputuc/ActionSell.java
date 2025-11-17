package marlkit.uputuc;

import agent.action.Action;
import marlkit.uputuc.unite.UniteConsumption;

public class ActionSell implements Action {

	private ResourceQuantify resourceQuantified;
	private UniteConsumption uniteCons;
	
	public ActionSell(ResourceQuantify resourceQuantified, UniteConsumption uniteCons) {
		super();
		this.resourceQuantified = resourceQuantified;
		this.uniteCons = uniteCons;
	}
	
	public TradeProposal getTradeProposal() {
		//TODO
		return null;
	}
	
	public ResourceQuantify getResourceQuantified() {
		return resourceQuantified;
	}
	public void setResourceQuantified(ResourceQuantify ressourceQuantified) {
		this.resourceQuantified = ressourceQuantified;
	}
	
	public Resource getResourceType() {
		return resourceQuantified.getType();
	}
	
	public int getResourceQuantity() {
		return resourceQuantified.getValue();
	}
	
	public void setRessourceQuantity(int value) {
		this.resourceQuantified.setValue(value);
	}

	public UniteConsumption getUniteCons() {
		return uniteCons;
	}

	public void setUniteCons(UniteConsumption uniteCons) {
		this.uniteCons = uniteCons;
	}
	
	
}
