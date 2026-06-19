package marlkit.foragingcontinuously.agents;

import communicationimplementation.BroadcastObservation;

public class AgentForagingBroadcastObservation extends AgentForagingCommunicating {
	public AgentForagingBroadcastObservation() {
		super(new BroadcastObservation());
	}

}
