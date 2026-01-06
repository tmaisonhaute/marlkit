package learningparameters;

public class FixedLearningRate implements LearningParameters {
	private double learningRate;
	
	public FixedLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
	
	public double getLearningRate() {
		return learningRate;
	}
	
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
}
