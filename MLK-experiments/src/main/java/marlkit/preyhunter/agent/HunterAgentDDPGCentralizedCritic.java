package marlkit.preyhunter.agent;

import centralizedtraining.CentralizedCriticTrainingExecutionStrategy;
import environment.observation.wrapperobservationvector.WrapperJointObservation;
import learning.ContinuousActionExplorationStrategy;
import learning.actionexplorationstrategies.GaussianNoise;
import learning.algorithms.DDPG;
import learning.nn.ActionValueCritic;
import learning.policies.MLPDeterministicPolicy;
import marlkit.preyhunter.environment.WrapperPreyHunterObservationVector;

public class HunterAgentDDPGCentralizedCritic extends HunterAgentDDPG {
	
	/**
	 * Constructor for a hunter agent with DDPG algorithm and centralized critic training.
	 * @param maxVisibleHunters number of other hunters that can be observed by this agent
	 * @param maxVisiblePreys number of preys that can be observed by this agent
	 * @param speed the speed of the hunter agent
	 */
	public HunterAgentDDPGCentralizedCritic(int maxVisibleHunters, int maxVisiblePreys, double speed, int nbHunterAgents) {
		super();

        WrapperPreyHunterObservationVector wrapperActor = new WrapperPreyHunterObservationVector(maxVisibleHunters, maxVisiblePreys);
        int observationSizeActor = wrapperActor.getVectorSize();
        
        ContinuousActionExplorationStrategy actionNoiseStrategy = new GaussianNoise(speed * NOISE_COEFFFICIENT);

        MLPDeterministicPolicy actor = createActor(wrapperActor, observationSizeActor, speed, actionNoiseStrategy);
        MLPDeterministicPolicy targetActor = createActor(wrapperActor, observationSizeActor, speed);
        
        WrapperJointObservation wrapperCritic = new WrapperJointObservation(wrapperActor, observationSizeActor, nbHunterAgents);
        int observationSizeCritic = wrapperCritic.getVectorSize();
        int actionSizeCritic = ACTION_SIZE * nbHunterAgents;

        ActionValueCritic critic = new ActionValueCritic(observationSizeCritic, actionSizeCritic, DEFAULT_CRITIC_HIDDEN_SIZE, wrapperCritic);
        ActionValueCritic targetCritic = new ActionValueCritic(observationSizeCritic, actionSizeCritic, DEFAULT_CRITIC_HIDDEN_SIZE, wrapperCritic);
        
        DDPG algorithm = new DDPG(actor, targetActor, critic, targetCritic, DEFAULT_ACTOR_LEARNING_RATE, 
        		DEFAULT_CRITIC_LEARNING_RATE, DEFAULT_GAMMA, DEFAULT_TAU, DEFAULT_LEARNING_BATCH_SIZE, DEFAULT_REPLAY_BUFFER_CAPACITY);

        setPolicy(actor);
        setAlgorithm(algorithm);
	}

	/**
	 * Requests a team-specific role for shared-experience centralized training.
	 */
	@Override
	protected void onActivation() {
		super.onActivation();
		requestRole(getCommunity(), getModelGroup(), CentralizedCriticTrainingExecutionStrategy.CENTRALIZED_CRITIC_AGENT_ROLE);
	}
}
