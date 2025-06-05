package util.criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import environment.state.State;

/**
 * A composite criterion that can combine multiple criteria with logical operations (AND, OR, NOT).
 * This class follows the Composite design pattern, allowing for tree-like structures of criteria.
 */
public class Criteria implements Criterion {
    private final List<Criterion> criteriaList = new ArrayList<>();
    private final Operation operation;
    
    public enum Operation {
        AND,
        OR,
        NOT
    }
  
    
    private Criteria(Operation operation, Criterion... criteria) {
        this.operation = operation;
        this.criteriaList.addAll(Arrays.asList(criteria));
        
        // Validate that NOT operation has exactly one criterion
        if (operation == Operation.NOT && criteriaList.size() != 1) {
            throw new IllegalArgumentException("NOT operation must have exactly one criterion");
        }
    }
    
    /**
     * Factory method to create an AND criteria composed of multiple criteria.
     * All criteria must be met for the composite to be met.
     */
    public static Criteria and(Criterion... criteria) {
        return new Criteria(Operation.AND, criteria);
    }
    
    /**
     * Factory method to create an OR criteria composed of multiple criteria.
     * At least one criterion must be met for the composite to be met.
     */
    public static Criteria or(Criterion... criteria) {
        return new Criteria(Operation.OR, criteria);
    }
    
    /**
     * Factory method to create a NOT criterion that negates the result of another criterion.
     * The composite is met when the wrapped criterion is not met.
     * 
     * @param criterion The criterion to negate
     * @return A new Criteria that negates the provided criterion
     */
    public static Criteria not(Criterion criterion) {
        return new Criteria(Operation.NOT, criterion);
    }
    
    @Override
    public void update(Optional<State> state) {
        for (Criterion criterion : criteriaList) {
            criterion.update(state);
        }
    }

	@Override
	public void reset() {
		for (Criterion criterion : criteriaList) {
			criterion.reset();
		}
	}
    
    @Override
    public boolean isMet() {
        if (criteriaList.isEmpty()) {
            // Empty AND is true (all conditions met), empty OR is false (no conditions met)
            return operation == Operation.AND;
        }
        
        switch (operation) {
            case AND:
                for (Criterion criterion : criteriaList) {
                    if (!criterion.isMet()) {
                        return false;
                    }
                }
                return true;
                
            case OR:
                for (Criterion criterion : criteriaList) {
                    if (criterion.isMet()) {
                        return true;
                    }
                }
                return false;
                
            case NOT:
                // For NOT operation, negate the result of the single criterion
                return !criteriaList.get(0).isMet();
                
            default:
                throw new IllegalStateException("Unknown operation: " + operation);
        }
    }
}