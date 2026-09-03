package experiment.configuration;

import java.util.Objects;

import experiment.configuration.agentspec.AgentSpec;

/**
 * Module responsible for creating the learning components of an experiment.
 * 
 * <p>
 * This module stores the concrete learning components creator selected for a configuration.
 * It uses a {@link LearningComponentsCreator} to create a fresh set of learning components for each agent group.
 * </p>
 */
public class LearningModule {

    private final LearningComponentsCreator learningComponentsCreator;

    /**
     * Creates a learning module with the specified learning components creator.
     * @param learningComponentsCreator the learning components creator to use for creating learning components
     * @throws NullPointerException if the learningComponentsCreator is null
     */
    public LearningModule(LearningComponentsCreator learningComponentsCreator) {
        this.learningComponentsCreator = Objects.requireNonNull(learningComponentsCreator, "learningComponentsCreator");
    }

    public LearningComponentsCreator getLearningComponents() {
        return learningComponentsCreator;
    }

    /**
     * Based on an {@link AgentSpec}, creates a fresh set of learning components for an agent group.
     * @param agentSpec the specification of the agent group for which to create learning components
     * @return a new set of learning components for the specified agent group
     */
    public LearningComponents createLearning(AgentSpec agentSpec) {
        return learningComponentsCreator.createLearning(agentSpec);
    }
}