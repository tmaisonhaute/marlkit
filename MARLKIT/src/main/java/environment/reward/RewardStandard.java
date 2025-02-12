package environment.reward;

public class RewardStandard implements Reward {
	protected double value;
	
	public RewardStandard(double val) {
		this.value = val;
	}
	
	@Override
	public double getReward() {
		// TODO Auto-generated method stub
		return value;
	}
	@Override
	public void setReward(double val) {
		this.value = val;
	}

}
