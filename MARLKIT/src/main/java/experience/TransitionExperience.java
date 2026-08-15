package experience;

import agent.action.Action;
import learning.policies.PolicyInput;
import reward.Reward;

public class TransitionExperience extends Experience {
	protected PolicyInput nextInput;
	protected boolean isTerminal;

	public TransitionExperience(PolicyInput input, Action action, Reward reward, PolicyInput nextInput, boolean isTerminal) {
		super(input, action, reward);
		this.nextInput = nextInput;
		this.isTerminal = isTerminal;
	}
	

    public PolicyInput getNextObservation() {
        return nextInput;
    }

    public boolean isTerminal() {
        return isTerminal;
    }
    
	@Override
	public TransitionExperience withAction(Action newAction) {
		return new TransitionExperience(this.input, newAction, this.reward, this.nextInput, this.isTerminal);
	}
	
	@Override
	public TransitionExperience withInputAction(PolicyInput newInput, Action newAction) {
		return new TransitionExperience(newInput, newAction, this.reward, this.nextInput, this.isTerminal);
	}

}
