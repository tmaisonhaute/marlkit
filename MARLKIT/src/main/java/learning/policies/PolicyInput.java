package learning.policies;

import environment.observation.Observation;
import learning.Policy;

/**
 * Policy Input is an interface for all class that may be used as Policy inputs. 
 * <p> 
 * Observation is the most common PolicyInput .
 * </p>
 * @see Policy
 * @see Observation
 */
public interface PolicyInput {

	/**
	 * Combines this PolicyInput with another one, returning a new PolicyInput that contains information from both.
	 *
	 * @param other the other PolicyInput to combine with this one
	 * @return a new PolicyInput that is the combination of this and the other
	 */
	PolicyInput add(PolicyInput other);
	
	public PolicyInput copy();
}
