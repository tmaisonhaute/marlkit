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
	
	@Override
	public RewardStandard clone() {
        return new RewardStandard(value);
	}

	@Override
	public void add(Reward other) {
		if (other instanceof RewardStandard rewardstandard) {
			this.value += rewardstandard.value;
		} else {
			throw new IllegalArgumentException("Cannot add different types of rewards");
		}
		
	}


	public void add(double val){
		value += val;
	}

}
