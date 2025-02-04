package agent.action;

public class actionInt implements Action {
	private int value;

	public actionInt(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
