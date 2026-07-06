package experiment.configuration;

import java.util.Objects;

import experiment.configuration.agentspec.AgentSpec;

public class LearningModule {

    private final LearningComponentsCreator learningComponentsCreator;

    public LearningModule(LearningComponentsCreator learningComponentsCreator) {
        this.learningComponentsCreator = Objects.requireNonNull(learningComponentsCreator, "learningComponentsCreator");
    }

    public LearningComponentsCreator getLearningComponents() {
        return learningComponentsCreator;
    }

    public LearningComponents createLearning(AgentSpec agentSpec) {
        return learningComponentsCreator.createLearning(agentSpec);
    }
}