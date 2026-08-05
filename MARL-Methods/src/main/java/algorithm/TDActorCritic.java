package algorithm;

import agent.MLKAgent;
import agent.action.Action;
import experience.Experience;
import learning.Batch;
import learning.Critic;
import learning.Policy;
import learning.algorithms.ActorCritic;
import learning.nn.StateValueCritic;
import learning.policies.ActorNetwork;
import learning.policies.PolicyInput;
import madkit.kernel.AgentLogger;



/**
 * TD Actor-Critic algorithm with:
 * - a stochastic discrete actor (policy network)
 * - a state-value critic V(s)
 *
 * The critic is updated with the TD(0) target:
 *     target = r + gamma * V(s')
 *
 * The actor is updated with the TD error:
 *     delta = r + gamma * V(s') - V(s)
 *
 * using the policy-gradient style loss:
 *     L = -delta * log pi(a|s)
 */

public class TDActorCritic implements ActorCritic {

	private final double gamma;
    private final double actorLearningRate;
    private final double criticLearningRate;

    private MLKAgent agent;
    private ActorNetwork actor;
    private final StateValueCritic critic;
    

    /**
     * Creates a TD Actor-Critic algorithm.
     *
     * @param actor the policy network
     * @param critic the state-value critic V(s)
     * @param actorLearningRate the actor learning rate
     * @param gamma the discount factor
     */
    public TDActorCritic(
            ActorNetwork actor,
            StateValueCritic critic,
            double actorLearningRate,
            double criticLearningRate,
            double gamma) {
        this.actor = actor;
        this.critic = critic;
        this.actorLearningRate = actorLearningRate;
        this.criticLearningRate = criticLearningRate;
        this.gamma = gamma;
    }

    /**
     * Creates a TD Actor-Critic algorithm with default parameters.
     *
     * @param actor the policy network
     * @param critic the state-value critic V(s)
     */
    public TDActorCritic(ActorNetwork actor, StateValueCritic critic) {
        this(actor, critic, 0.0001, 0.0001, 0.95);
    }

	@Override
	public void setPolicy(Policy policy) {
        if (policy instanceof ActorNetwork actorNetwork) {
            this.actor = actorNetwork;
            if (agent != null) {
                this.actor.init(agent);
            }
        } else {
            throw new IllegalArgumentException("TDActorCritic requires an ActorNetwork as policy.");
        }
	}

	@Override
	public Policy getPolicy() {
		return getActor();
	}
	
	@Override
	public void setAgent(MLKAgent agent) {
		this.agent = agent;
	}

	@Override
	public MLKAgent getAgent() {
		return agent;
	}

	@Override
	public int getLearningFrequency() {
		return 1;
	}

	@Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
		while (batch.getExperiences().size() > 1) {
            handleTransition(batch);
        }
	}
	

    /**
     * Processes one transition (s, a, r, s') from the first two experiences in the batch.
     */
    private void handleTransition(Batch batch) {
        Experience currentExperience = batch.getExperiences().get(0);
        Experience nextExperience = batch.getExperiences().get(1);
        
        Experience criticCurrentExperience = critic.getEnrichedExperience(currentExperience);
        Experience criticNextExperience = critic.getEnrichedExperience(nextExperience);
        
        criticCurrentExperience = criticCurrentExperience != null ? criticCurrentExperience : currentExperience;
        criticNextExperience = criticNextExperience != null ? criticNextExperience : nextExperience;

        PolicyInput currentInput = currentExperience.getInput();
        Action selectedAction = currentExperience.getAction();
        double reward = currentExperience.getRewardValue();

        double tdError = critic.updateFromTransition(
        		criticCurrentExperience.getInput(),
                reward,
                criticNextExperience.getInput(),
                false,
                gamma, 
                getCriticLearningRate()
        );

        updateActor(currentInput, selectedAction, tdError);

        batch.getExperiences().removeFirst();
        getCritic().removeEnrichedExperience(currentExperience);
    }


    /**
     * Updates the actor using the TD error as advantage estimate.
     *
     * Gradient corresponds to:
     *     L = -tdError * log pi(a|s)
     *
     * Therefore:
     *     dL/dlogits = tdError * (probs - oneHot(action))
     */
    private void updateActor(PolicyInput input, Action selectedAction, double tdError) {
        double[] logits = actor.forwardLogits(input);
        double[] probs = actor.softmax(logits);

        int actionIndex = actor.getActionSet().indexOf(selectedAction);
        if (actionIndex < 0) {
            throw new IllegalArgumentException("Selected action is not in actor action set.");
        }

        double[] dLossDLogits = new double[probs.length];
        for (int i = 0; i < probs.length; i++) {
            dLossDLogits[i] = tdError * probs[i];
        }
        dLossDLogits[actionIndex] -= tdError;

        actor.updateFromLogitsGradient(input, dLossDLogits, getActorLearningRate());
    }




	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
        if (batch.getExperiences().size() == 1) {
            Experience last = batch.getExperiences().get(0);
            
            Experience criticLastExperience = critic.getEnrichedExperience(last);
            criticLastExperience = criticLastExperience != null ? criticLastExperience : last;

            PolicyInput currentInput = last.getInput();
            Action selectedAction = last.getAction();
            double reward = last.getRewardValue();

            
            double tdError = critic.updateFromTransition(
            		criticLastExperience.getInput(),
                    reward,
                    null,
                    true,
                    gamma,
                    getCriticLearningRate()
            );

            updateActor(currentInput, selectedAction, tdError);
        }
        batch.clear();
        critic.clearEnrichedExperiences();

	}
	
	public double getActorLearningRate() {
		return actorLearningRate;
	}
	
	public double getCriticLearningRate() {
        return criticLearningRate;
    }
	
	@Override
	public ActorNetwork getActor() {
		return actor;
	}
	
	@Override
	public Critic getCritic() {
		return critic;
	}

}
