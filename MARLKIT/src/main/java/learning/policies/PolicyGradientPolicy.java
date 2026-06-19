package learning.policies;

import agent.action.Action;
import learning.Policy;
import util.VectorOperator;

public interface PolicyGradientPolicy extends Policy {

    double[][] forwardLogits(PolicyInput[] inputs);

    void updateFromLogitsGradient(PolicyInput[] inputs, double[][] dLossDLogits, double learningRate);
    
    public default double[][] softmax(double[][] logits){
    	return VectorOperator.softmax(logits, getSoftmaxTemperature());
    }
    
    double getSoftmaxTemperature();

    int actionIndex(Action action);
	
}
