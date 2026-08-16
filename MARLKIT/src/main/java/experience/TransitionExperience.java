package experience;

import java.util.Map;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import environment.observation.MappedJointObservation;
import environment.observation.Observation;
import learning.policies.PolicyInput;
import reward.Reward;

public class TransitionExperience implements Experience {
	protected PolicyInput input;
    protected Action action;
    protected Reward reward;
	protected PolicyInput nextInput;
	protected boolean isTerminal;

	public TransitionExperience(PolicyInput input, Action action, Reward reward, PolicyInput nextInput, boolean isTerminal) {
		this.input = input;
		this.action = action;
		this.reward = reward;
		this.nextInput = nextInput;
		this.isTerminal = isTerminal;
	}
	

	@Override
	public PolicyInput getInput() {
		return input;
	}

	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public Reward getReward() {
		return reward;
	}

	@Override
	public Double getRewardValue() {
		return reward.getValue();
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws NullPointerException if an argument is {@code null}
	 * @throws IllegalArgumentException if an experience is not a transition or if a current or next input is not an observation
	 */
	public TransitionExperience createCentralizedExperience(Map<MLKAgent, Experience> experiencesByAgent, Reward centralReward) {
	    Objects.requireNonNull(experiencesByAgent, "experiencesByAgent");
	    Objects.requireNonNull(centralReward, "centralReward");

	    MappedJointObservation jointObservation = new MappedJointObservation();
	    MappedJointObservation jointNextObservation = new MappedJointObservation();
	    MappedJointAction jointAction = new MappedJointAction();

	    for (Map.Entry<MLKAgent, Experience> entry : experiencesByAgent.entrySet()) {
	        MLKAgent agent = entry.getKey();
	        Experience experience = entry.getValue();

	        if (!(experience instanceof TransitionExperience transition)) {
	            throw new IllegalArgumentException("Centralized transitions require only TransitionExperience instances.");
	        }

	        if (!(transition.getInput() instanceof Observation observation)) {
	            throw new IllegalArgumentException("Centralized transitions require Observation inputs.");
	        }

	        if (!(transition.getNextObservation() instanceof Observation nextObservation)) {
	            throw new IllegalArgumentException("Centralized transitions require Observation next inputs.");
	        }

	        jointObservation.addObservation(agent, observation);
	        jointNextObservation.addObservation(agent, nextObservation);
	        jointAction.addAction(agent, transition.getAction());
	    }

	    return new TransitionExperience(jointObservation, jointAction, centralReward, jointNextObservation, isTerminal);
	}



}
