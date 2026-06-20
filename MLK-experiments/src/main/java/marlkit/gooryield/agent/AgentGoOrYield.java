package marlkit.gooryield.agent;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;
import marlkit.gooryield.agent.action.ActionGo;
import marlkit.gooryield.agent.action.ActionYield;

public class AgentGoOrYield extends AgentStandard {
    protected Action yield = new ActionYield();
    protected Action go = new ActionGo();
    protected List<Action> possibleActions = new ArrayList<>(List.of(yield, go));

    public AgentGoOrYield() {
        super();
    }

    @Override
    protected void onActivation() {
        initPolicyAndAlgorithm();
        super.onActivation();
    }

    protected void initPolicyAndAlgorithm() {
        QValueBasedPolicy qPolicy = new QValueBasedPolicy(possibleActions, 1.0);
        setExplorationStrategy(qPolicy);
        
        setPolicy(qPolicy);
        setupAlgorithm(qPolicy);
    }
    
    protected void setExplorationStrategy(QValueBasedPolicy qPolicy) {
    	 qPolicy.setExplorationStrategy(new EpsilonGreedyExponentialDecay(1.0, 0.001));
//    	qPolicy.setExplorationStrategy(new EpsilonGreedyFix(0.05));
    }
    protected void setupAlgorithm(QValueBasedPolicy qPolicy) {
    	QLearning qLearning = new QLearning(qPolicy, possibleActions, 0.2, 0.95);
    	setAlgorithm(qLearning);
    }
}