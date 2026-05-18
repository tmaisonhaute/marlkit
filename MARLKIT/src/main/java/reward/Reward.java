package reward;

/**
 * Represents a reward signal in reinforcement learning.
 */
public interface Reward {
	/**
	 * Returns the numeric value of this reward.
	 *
	 * @return the reward value
	 */
	public double getValue();
	
	/**
	 * Sets the reward value.
	 *
	 * @param val the new reward value
	 */
	public void setReward(double val);
	
	/**
	 * Adds another reward to this reward.
	 *
	 * @param other the reward to add
	 */
	public void add(Reward other);
	
	/**
	 * Adds a numeric value to this reward.
	 *
	 * @param val the value to add
	 */
	public default void add(double val) {
        setReward(getValue() + val);
    }
	
	/**
	 * Creates a copy of this reward.
	 *
	 * @return a cloned reward
	 */
	public Reward clone();
}
