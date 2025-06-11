package util.criteria;

/**
 * A criterion that is met when the current time modulo some divisor equals a specific remainder.
 * Useful for triggering events at regular intervals.
 */
public class ModuloTimeCriterion extends TimeCounterCriterion {
    private final int modulo;
    private final int remainder;
    
    /**
     * Creates a criterion that is met when currentTime % modulo == remainder
     * 
     * @param modulo The divisor for the modulo operation
     * @param remainder The expected remainder (must be less than modulo)
     */
    public ModuloTimeCriterion(int modulo, int remainder) {
    	super();
        if (modulo <= 0) {
            throw new IllegalArgumentException("Modulo must be positive");
        }
        if (remainder < 0) {
            throw new IllegalArgumentException("Remainder must be positive ");
        }
        this.modulo = modulo;
        this.remainder = remainder;
    }
    
	/**
	 * Creates a criterion that is met when currentTime % modulo == 0
	 * 
	 * @param modulo The divisor for the modulo operation
	 */
    public ModuloTimeCriterion(int modulo) {
    	this(modulo, 0);
    }
    
    
    /**
     * Checks if the criterion has been met.
     * 
     * @return true if the current time modulo divisor is equal to the remainder, false otherwise
     */
    @Override
    public boolean isMet() {
        return getCurrentTime() % modulo == remainder;
    }
    
    /**
     * @return The modulo value used in this criterion
     */
    public int getModulo() {
        return modulo;
    }
    
    /**
     * @return The remainder value this criterion is looking for
     */
    public int getRemainder() {
        return remainder;
    }
    
}
