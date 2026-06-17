package learning.algorithms;

import agent.MLKAgent;
import learning.Algorithm;
import learning.Critic;
import learning.policies.ActorNetwork;

public interface ActorCritic extends Algorithm {

	public ActorNetwork getActor();
	public Critic getCritic();
	
	@Override
	public default void init(MLKAgent agent) {
		setAgent(agent);
        getActor().init(agent);
        getCritic().init(agent);
	}
}
