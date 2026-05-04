package marlkit.trade;

import java.util.Objects;

import agent.action.Action;

public class ActionRequestResource implements Action {

    private UniteProduction uniteProduction;

    public ActionRequestResource(UniteProduction uniteProduction) {
        this.uniteProduction = uniteProduction;
    }
    public UniteProduction getUniteProduction() {
        return uniteProduction;
    }
    public void setUniteProduction(UniteProduction uniteProduction) {
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
