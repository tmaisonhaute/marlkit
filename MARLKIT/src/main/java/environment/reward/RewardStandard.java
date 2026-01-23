package environment.reward;

/**
 * Standard implementation of a reward as a double value.
 */
public class RewardStandard implements Reward {
	protected double value;
	
	/**
	 * Creates a new reward with the specified value.
	 *
	 * @param val the reward value
	 */
	public RewardStandard(double val) {
		this.value = val;
	}
	
	/**
	 * Creates a new reward with value 0.
	 */
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

	@Override
	public void add(double val){
		value += val;
	}

}
