package environment.reward;

public class RewardStandard implements Reward {
	protected double value;
	
	public RewardStandard(double val) {
		this.value = val;
	}
	public RewardStandard() {
		this.value = 0;
	}

	@Override
	public double getValue() {
		return value;
	}
	@Override
	public void setReward(double val) {
		this.value = val;
	}

	public void add(double val){
		value += val;
	}

}
