package learning.algorithm;

import agent.MLKAgent;
import learning.Critic;
import learning.nn.ActorNetwork;

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
