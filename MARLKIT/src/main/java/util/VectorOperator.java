package util;

public class VectorOperator {

	public static double[][] split(double[] vector, int objectSize) {
        int n = vector.length / objectSize;

        double[][] entities = new double[n][objectSize];

        for (int i = 0; i < n; i++) {
            System.arraycopy(vector, i * objectSize, entities[i], 0, objectSize);
        }

        return entities;
    }
	
	/**
	 * Computes the softmax of the given logits with temperature scaling.
	 * @param logits the raw scores
	 * @param temperature the temperature parameter for scaling
	 * @return the softmax probability distribution
	 */
	public static double[] softmax(double[] logits, double temperature) {
        double max = Double.NEGATIVE_INFINITY;

        for (double v : logits) {
            max = Math.max(max, v / temperature);
        }

        double sum = 0.0;
        double[] exp = new double[logits.length];

        for (int i = 0; i < logits.length; i++) {
            exp[i] = Math.exp((logits[i] / temperature) - max);
            sum += exp[i];
        }

        for (int i = 0; i < exp.length; i++) {
            exp[i] /= sum;
        }

        return exp;
    }
	

	/**
	 * Computes the softmax of a 2D array of logits with temperature scaling.
	 * @param logits the 2D array of raw scores
	 * @param temperature the temperature parameter for scaling
	 * @return the 2D array of softmax probability distributions
	 */
    public static double[][] softmax(double[][] logits, double temperature){
	    double[][] probs = new double[logits.length][];
	
	    for (int i = 0; i < logits.length; i++) {
	        probs[i] = softmax(logits[i], temperature);
	    }
	
	    return probs;
    }
	
	/**
     * Concatenates two vectors.
	 * @param a the first vector
	 * @param b the second vector
	 * @return the concatenate vector
	 */
	public static double[] concatenate(double[] a, double[] b) {
        double[] out = new double[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
	
}
