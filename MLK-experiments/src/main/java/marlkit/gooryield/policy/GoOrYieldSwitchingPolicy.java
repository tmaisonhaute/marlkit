package marlkit.gooryield.policy;

import java.util.ArrayList;
import java.util.List;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Policy;
import learning.policies.StochasticFixedPolicy;

public class GoOrYieldSwitchingPolicy implements Policy {

    protected MLKAgent agent;
    protected final List<StochasticFixedPolicy> availablePolicies;
    protected StochasticFixedPolicy currentPolicy;

    public GoOrYieldSwitchingPolicy() {
        this.availablePolicies = new ArrayList<>();
        
    }
    

    @Override
    public void init(MLKAgent agent) {
    	this.agent = agent;
    	
    	this.availablePolicies.add(new AlwaysGoPolicy(prng()));
    	this.availablePolicies.add(new Go75Yield25Policy(prng()));
    	this.availablePolicies.add(new Go25Yield75Policy(prng()));
    	this.availablePolicies.add(new AlwaysYieldPolicy(prng()));
    	this.availablePolicies.add(new Go50Yield50Policy(prng()));
        this.currentPolicy = availablePolicies.get(0);
        
        for (StochasticFixedPolicy policy : availablePolicies) {
            policy.init(agent);
        }
        selectRandomPolicy();
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    @Override
    public Action selectAction(Observation input) {
        return currentPolicy.selectAction(input);
    }

    /**
     * Selects a random policy from the available policies and sets it as the current policy.
     */
    public void selectRandomPolicy() {
        int index = prng().nextInt(availablePolicies.size());
        currentPolicy = availablePolicies.get(index);
    }
    
    /**
     * Selects the next policy in the list of available policies and sets it as the current policy.
     */
	public void selectNextPolicy() {
		int currentIndex = availablePolicies.indexOf(currentPolicy);
		int nextIndex = (currentIndex + 1) % availablePolicies.size();
		currentPolicy = availablePolicies.get(nextIndex);
	}

    public StochasticFixedPolicy getCurrentPolicy() {
        return currentPolicy;
    }

    public List<StochasticFixedPolicy> getAvailablePolicies() {
        return new ArrayList<>(availablePolicies);
    }
}