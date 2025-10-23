package marlkit.uputuc;

import agent.action.Action;
import marlkit.uputuc.unite.UniteProduction;

public class ActionBuy implements Action {
	private ResourceQuantify resourceQuantified;
	private UniteProduction uniteProd;
	
	public ActionBuy(ResourceQuantify resourceQuantified, UniteProduction uniteProd) {
		super();
		this.resourceQuantified = resourceQuantified;
		this.uniteProd = uniteProd;
	}
	
	public TradeProposal getTradeProposal() {
		return new TradeProposal(getResourceType(),getResourceQuantity(), 0f, null, null, uniteProd);
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
	
	public UniteProduction getUniteProd() {
		return uniteProd;
	}
	public void setUniteProd(UniteProduction uniteProd) {
		this.uniteProd = uniteProd;
	}

}
