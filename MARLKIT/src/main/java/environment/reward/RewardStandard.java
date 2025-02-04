package environment.reward;

public class RewardStandard implements Reward {
	private double value;
	
	public RewardStandard(double val) {
		this.value = val;
	}
	
	@Override
	public double getValue() {
		// TODO Auto-generated method stub
		return value;
	}
	
	public void setValue(double val) {
		this.value = val;
	}

}
