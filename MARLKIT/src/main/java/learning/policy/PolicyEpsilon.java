package learning.policy;

public abstract class PolicyEpsilon implements Policy {

	protected double epsilonBase;
	protected double epsilonReduction;
	protected int nbIterations;
	
	protected PolicyEpsilon(double epsilonBase, double epsilonDecrease) {
        this.epsilonBase = epsilonBase;
        this.epsilonReduction = epsilonDecrease;
        this.nbIterations = 0;
	}
	
	protected PolicyEpsilon(double epsilonBase) {
        this(epsilonBase, 0.0);
	}
	
	protected PolicyEpsilon() {
        this(0.05);
    }
	
	public double getEpsilon() {
		return epsilonBase / (1 + (nbIterations) * epsilonReduction);
	}

	public void setEpsilon(double epsilon) {
		double currentEpsilon = getEpsilon();
		if (currentEpsilon == 0) {
			epsilonBase = epsilon;
		}else {
			epsilonBase *= epsilon/currentEpsilon;
		}
	}

	public void updateEpsilon() {
		nbIterations++;
	}
	
	public int getNbIterations() {
        return nbIterations;
    }
	
}
