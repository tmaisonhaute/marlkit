package learning;

import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;

public class Experience {
    private Observation observation;
    private Action action;
    private Reward reward;

    public Experience(Observation observation, Action action, Reward reward) {
        this.observation = observation;
        this.action = action;
        this.reward = reward;
    }

    public Observation getObservation() {
        return observation;
    }

    public Action getAction() {
        return action;
    }

    public Reward getReward() {
        return reward;
    }

	public Double getRewardValue() {
		return reward.getValue();
	}
}