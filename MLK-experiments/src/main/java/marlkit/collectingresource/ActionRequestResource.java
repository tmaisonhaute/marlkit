package marlkit.collectingresource;

import java.util.Objects;

import agent.action.Action;
import marlkit.collectingresource.environment.ProductionUnit;

public class ActionRequestResource implements Action {

    private ProductionUnit uniteProduction;

    public ActionRequestResource(ProductionUnit uniteProduction) {
        this.uniteProduction = uniteProduction;
    }
    public ProductionUnit getUniteProduction() {
        return uniteProduction;
    }
    public void setUniteProduction(ProductionUnit uniteProduction) {
        this.uniteProduction = uniteProduction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniteProduction);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ActionRequestResource arr) {
            return this.uniteProduction.equals(arr.uniteProduction);
        }
        return false;
    }
	@Override
	public Action copy() {
		return new ActionRequestResource(this.uniteProduction);
	}
}
