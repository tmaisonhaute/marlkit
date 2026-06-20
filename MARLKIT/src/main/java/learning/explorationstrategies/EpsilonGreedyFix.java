package learning.explorationstrategies;

public class EpsilonGreedyFix extends EpsilonGreedy {

	protected double epsilon;
	public EpsilonGreedyFix(double epsilon) {
		super();
		this.epsilon = epsilon;
	}
	
	@Override
	public double getEpsilon() {
		return epsilon;
	}

}
