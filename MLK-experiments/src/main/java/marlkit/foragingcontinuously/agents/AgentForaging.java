package marlkit.foragingcontinuously.agents;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import environment.observation.wrapperobservationvector.WrapperVectorObservationPositionsValues;
import learning.algorithms.Reinforce;
import learning.nn.NeuralNetwork;
import learning.policies.ObjectVotingNeuralPolicy;

public class AgentForaging extends AgentStandard {
	List<Action> possibleActions = new ArrayList<>(List.of(Move2D.left(), Move2D.right(), Move2D.up(), Move2D.down(), Move2D.idle()));

	public AgentForaging() {
		super();
//		QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyPowerDecay(0.5));
//		SoftmaxQPolicy policy = new SoftmaxQPolicy(possibleActions, 1.0, 5, new EpsilonGreedyPowerDecay(0.5));
//    	QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.995);
    	

    	WrapperPolicyInputVector wrapper = new WrapperVectorObservationPositionsValues(true);

        NeuralNetwork network = NeuralNetwork.reluIdentity(new int[] {
            3,
            32,
            32,
            possibleActions.size()
        });

        ObjectVotingNeuralPolicy policy = new ObjectVotingNeuralPolicy(
            network,
            wrapper,
            possibleActions.toArray(new Action[0]),
            5.0,
            true
        );

        Reinforce algorithm = new Reinforce(
            policy,
            0.001,
            0.995
        );

    	
    	setPolicy(policy);
    	setAlgorithm(algorithm);
	}
}
