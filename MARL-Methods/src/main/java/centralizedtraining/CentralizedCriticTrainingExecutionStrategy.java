package centralizedtraining;

import java.util.Collection;
import java.util.List;

import agent.MLKAgent;
import madkit.kernel.Activator;
import madkit.simulation.scheduler.MethodActivator;
import trainingexecutionstrategy.TrainingExecutionStrategy;

/**
* Implements a centralized-critic training and execution strategy.
*
* <p>Agents register their observations, update their policies, and process
* episode endings individually. During experience collection, however, their
* local experiences are combined into centralized experiences by a
* {@link CentralizedCriticCollectExperienceActivator} activator.</p>
*
* <p>This strategy therefore supports centralized critic training while
* preserving decentralized observation registration and policy execution.</p>
*/
public class CentralizedCriticTrainingExecutionStrategy implements TrainingExecutionStrategy {
	/**
	* Role assigned to agents whose experiences are processed by the
	* centralized critic experience collector.
	*/
	public static final String CENTRALIZED_CRITIC_AGENT_ROLE = "AgentCentralizedCritic";
	
	private CentralizedCriticCollectExperienceActivator centralizedCriticCollectExperience;
	
	private Activator agentsMakeObservation;
    private Activator agentsUpdatePolicy;
    private Activator agentsEndEpisode;

	@Override
	public void activate(String modelGroup) {
		centralizedCriticCollectExperience = new CentralizedCriticCollectExperienceActivator(modelGroup, CENTRALIZED_CRITIC_AGENT_ROLE);
		
		agentsMakeObservation = new MethodActivator(modelGroup, MLKAgent.DEFAULT_AGENT_ROLE, "registerObservation");
    	agentsUpdatePolicy = new MethodActivator(modelGroup, MLKAgent.DEFAULT_AGENT_ROLE, "updatePolicy");
    	agentsEndEpisode = new MethodActivator(modelGroup, MLKAgent.DEFAULT_AGENT_ROLE, "endEpisode");

	}

	@Override
	public void agentsMakeObservation() {
		agentsMakeObservation.execute();
	}

	@Override
	public void agentsCollectExperience() {
		centralizedCriticCollectExperience.execute();
	}

	@Override
	public void agentsUpdatePolicy(int simulationStep) {
		agentsUpdatePolicy.execute(simulationStep);
	}

	@Override
	public void agentsEndEpisode() {
		agentsEndEpisode.execute();
	}

	@Override
	public Collection<Activator> getActivators() {
		return List.of(agentsMakeObservation, centralizedCriticCollectExperience, agentsUpdatePolicy, agentsEndEpisode);
	}

}
