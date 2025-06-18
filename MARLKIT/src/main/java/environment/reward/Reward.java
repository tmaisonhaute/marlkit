package environment.reward;

/**
 * Represents a reward in the environment.
 */
public interface Reward {
	public double getValue();
	public void setReward(double val);
	public void add(Reward other);
	public default void add(double val) {
        setReward(getValue() + val);
    }
	public Reward clone();
}
