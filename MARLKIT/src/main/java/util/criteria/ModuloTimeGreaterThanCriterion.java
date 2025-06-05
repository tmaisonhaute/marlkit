package util.criteria;

/**
 * A criterion that is met when the current time modulo some divisor is greater than a specified threshold.
 * Useful for triggering events within specific portions of periodic time intervals.
 */
public class ModuloTimeGreaterThanCriterion extends TimeCounterCriterion{
    private final int modulo;
    private final int threshold;

    /**
     * Creates a criterion that is met when (currentTime % modulo) > threshold
     * 
     * @param modulo The divisor for the modulo operation
     * @param threshold The threshold value to compare against (must be less than modulo)
     */
    public ModuloTimeGreaterThanCriterion(int modulo, int threshold) {
    	super();
        if (modulo <= 0) {
            throw new IllegalArgumentException("Modulo must be positive");
        }
        if (threshold < 0 || threshold >= modulo) {
            throw new IllegalArgumentException("Threshold must be between 0 and " + (modulo - 1));
        }
        this.modulo = modulo;
        this.threshold = threshold;
    }
    
    


    /**
     * Checks if the criterion has been met.
     * 
     * @return true if the current time modulo divisor is greater than the threshold, false otherwise
     */
    @Override
    public boolean isMet() {
        return getCurrentTime() % modulo > threshold;
    }
    
    /**
     * @return The modulo value used in this criterion
     */
    public int getModulo() {
        return modulo;
    }
    
    /**
     * @return The threshold value this criterion uses for comparison
     */
    public int getThreshold() {
        return threshold;
    }
    
}
