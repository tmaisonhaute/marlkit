package marlkit.preyhunter.experiments;

import experiment.configuration.LearningComponents;
import experiment.configuration.LearningComponentsCreator;
import experiment.configuration.agentspec.AgentSpec;
import experiment.configuration.agentspec.AgentSpecInputSize;
import experiment.configuration.agentspec.AgentSpecInputWrapper;
import learning.algorithms.PPOCategorical;
import learning.policies.NeuralNetworkCategoricalPolicy;

public class HunterPPOLearningComponentsCreator extends LearningComponentsCreator {

    private static final double DEFAULT_SOFTMAX_TEMPERATURE = 1.0;

    private static final double DEFAULT_LEARNING_RATE = 0.001;
    private static final double DEFAULT_GAMMA = 0.95;
    private static final double DEFAULT_CLIP_EPSILON = 0.2;
    private static final int DEFAULT_PPO_EPOCHS = 4;

    private static final int[] DEFAULT_HIDDEN_LAYERS = new int[] { 32, 32 };

    @Override
    public LearningComponents createLearning(AgentSpec agentSpec) {
        if (!(agentSpec instanceof AgentSpecInputSize inputSizeSpec)) {
            throw new IllegalArgumentException("PreyHunter PPO requires an AgentSpecInputSize.");
        }

        if (!(agentSpec instanceof AgentSpecInputWrapper inputWrapperSpec)) {
            throw new IllegalArgumentException("PreyHunter PPO requires an AgentSpecInputWrapper.");
        }

        NeuralNetworkCategoricalPolicy policy = new NeuralNetworkCategoricalPolicy(
                agentSpec.getPossibleActions(),
                inputWrapperSpec.getInputWrapper(),
                inputSizeSpec.getInputSize(),
                DEFAULT_HIDDEN_LAYERS,
                DEFAULT_SOFTMAX_TEMPERATURE
        );

        PPOCategorical algorithm = new PPOCategorical(
                policy,
                DEFAULT_LEARNING_RATE,
                DEFAULT_GAMMA,
                DEFAULT_CLIP_EPSILON,
                DEFAULT_PPO_EPOCHS
        );

        return new LearningComponents(policy, algorithm);
    }
}