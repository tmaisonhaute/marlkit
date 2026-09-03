package experiment.configuration;

import experiment.configuration.agentspec.AgentSpec;

/**
 * Abstract class responsible for creating learning components for an agent group based on its specification.
 */
public abstract class LearningComponentsCreator {

	/**
	 * Creates a fresh set of learning components for an agent group based on a given {@link AgentSpec}.
	 * @param agentSpec the specification of the agent group for which to create learning components
	 * @return a new set of learning components for the specified agent group
	 */
	public abstract LearningComponents createLearning(AgentSpec agentSpec);
}
