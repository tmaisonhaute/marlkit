package experience;

import java.util.Map;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.MappedJointAction;
import environment.observation.MappedJointObservation;
import environment.observation.Observation;
import reward.Reward;

public class TransitionExperience implements Experience {
	protected Observation observation;
    protected Action action;
    protected Reward reward;
	protected Observation nextObservation;
	protected boolean isTerminal;

	public TransitionExperience(Observation observation, Action action, Reward reward, Observation nextObservation, boolean isTerminal) {
		this.observation = observation;
		this.action = action;
		this.reward = reward;
		this.nextObservation = nextObservation;
		this.isTerminal = isTerminal;
	}
	

	@Override
	public Observation getObservation() {
		return observation;
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
	
    public Observation getNextObservation() {
        return nextObservation;
    }

    public boolean isTerminal() {
        return isTerminal;
    }
    
	@Override
	public TransitionExperience withAction(Action newAction) {
		return new TransitionExperience(this.observation, newAction, this.reward, this.nextObservation, this.isTerminal);
	}
	
	@Override
	public TransitionExperience withObservationAction(Observation newObservation, Action newAction) {
		return new TransitionExperience(newObservation, newAction, this.reward, this.nextObservation, this.isTerminal);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws NullPointerException if an argument is {@code null}
	 * @throws IllegalArgumentException if an experience is not a transition
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

	        jointObservation.addObservation(agent, observation);
	        jointNextObservation.addObservation(agent, nextObservation);
	        jointAction.addAction(agent, transition.getAction());
	    }

	    return new TransitionExperience(jointObservation, jointAction, centralReward, jointNextObservation, isTerminal);
	}



}
