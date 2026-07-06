package experiment.configuration;

import experiment.configuration.agentspec.AgentSpec;

public abstract class LearningComponentsCreator {

	public abstract LearningComponents createLearning(AgentSpec agentSpec);
}
