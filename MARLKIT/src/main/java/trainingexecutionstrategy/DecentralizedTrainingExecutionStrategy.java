package trainingexecutionstrategy;

import java.util.Collection;
import java.util.List;

import agent.MLKAgent;
import madkit.kernel.Activator;
import madkit.simulation.scheduler.MethodActivator;

public class DecentralizedTrainingExecutionStrategy implements TrainingExecutionStrategy {

    private Activator agentsMakeObservation;
    private Activator agentsCollectExperience;
    private Activator agentsUpdatePolicy;
    private Activator agentsEndEpisode;
    private Activator agentsAct;

    @Override
    public void activate(String modelGroup) {
    	agentsMakeObservation = new MethodActivator(modelGroup, MLKAgent.DEFAULT_AGENT_ROLE, "registerObservation");
    	agentsCollectExperience = new MethodActivator(modelGroup, MLKAgent.DEFAULT_AGENT_ROLE, "collectExperience");
    	agentsUpdatePolicy = new MethodActivator(modelGroup, MLKAgent.DEFAULT_AGENT_ROLE, "updatePolicy");
    	agentsEndEpisode = new MethodActivator(modelGroup, MLKAgent.DEFAULT_AGENT_ROLE, "endEpisode");
    	agentsAct = new MethodActivator(modelGroup, MLKAgent.DEFAULT_AGENT_ROLE, "takeAction");
    }

    @Override
    public void agentsMakeObservation() {
        agentsMakeObservation.execute();
    }
    
    @Override
	public void agentsAct() {
		agentsAct.execute();
	}

    @Override
    public void agentsCollectExperience() {
        agentsCollectExperience.execute();
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
		return List.of(agentsMakeObservation, agentsAct, agentsCollectExperience, agentsUpdatePolicy, agentsEndEpisode);
	}

}


