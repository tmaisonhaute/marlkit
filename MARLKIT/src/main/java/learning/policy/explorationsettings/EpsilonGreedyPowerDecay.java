package learning.policy.explorationsettings;

public class EpsilonGreedyPowerDecay extends EpsilonGreedy {

	protected double power;
	
	public EpsilonGreedyPowerDecay(double power) {
		super();
		this.power = power;
	}
	
	@Override
	public double getEpsilon() {
		return 1/(Math.pow(getNbIterations(), power));
	}

}
