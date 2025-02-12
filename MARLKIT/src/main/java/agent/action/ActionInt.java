package agent.action;

public class ActionInt implements Action {
	protected int value;

	public ActionInt(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
