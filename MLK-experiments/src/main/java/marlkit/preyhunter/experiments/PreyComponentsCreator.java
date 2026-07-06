package marlkit.preyhunter.experiments;

import experiment.configuration.LearningComponents;
import experiment.configuration.LearningComponentsCreator;
import experiment.configuration.agentspec.AgentSpec;
import learning.algorithms.NoLearningAlgorithm;
import marlkit.preyhunter.policies.PreyEscapePolicy;

public class PreyComponentsCreator extends LearningComponentsCreator {
	    private final double speed;

	    public PreyComponentsCreator(double speed) {
	        this.speed = speed;
	    }

	    @Override
	    public LearningComponents createLearning(AgentSpec agentSpec) {
	        PreyEscapePolicy policy = new PreyEscapePolicy(speed);
	        NoLearningAlgorithm algorithm = new NoLearningAlgorithm(policy);

	        return new LearningComponents(policy, algorithm);
	    }
	}