package simulation;

import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.Optional;
import java.util.logging.Level;

import environment.MLKEnvironment;
import environment.state.State;
import madkit.kernel.Activator;
import madkit.simulation.SimuOrganization;
import madkit.simulation.scheduler.MethodActivator;
import madkit.simulation.scheduler.TickBasedScheduler;
import util.criteria.Criterion;

/**
 * Scheduler for MARLKIT simulations that coordinates agent-environment interactions.
 * Manages the execution cycle of observations, actions, learning, and episode boundaries.
 */
public abstract class MLKScheduler extends TickBasedScheduler {
	private MLKEnvironment env;
	
	private Activator initEnvironment;
	private Activator computeObservations;
	private Activator step;
	private Activator reset;
	private Activator clearPreviousStepVariables;
	private Activator envEndEpisode;
	private Activator envEnd;
	
	private Activator agentAct;
	private Activator agentCollectExperience;
	private Activator agentUpdatePolicy;
	private Activator agentEndEpisode;

	private MethodActivator viewers;
	
	private int counter = 0;

	/**
	 * Initializes the scheduler and sets up activators for agent and environment actions.
	 */
	@Override
	protected void onActivation() {
		getLogger().setLevel(Level.INFO);
		super.onActivation();
		final String roleAgent = "mlkagent";
		
		initEnvironment = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "init");
		addActivator(initEnvironment);
		computeObservations = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "computeObservations");
		addActivator(computeObservations);
		step = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "step");
		addActivator(step);
		clearPreviousStepVariables = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "clearStepVariables");
		addActivator(clearPreviousStepVariables);
		reset = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "reset");
		addActivator(reset);
		envEndEpisode = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "onEpisodeEnd");
		addActivator(envEndEpisode);
		envEnd = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "onEnd");
		addActivator(envEnd);
		
		agentAct = new MethodActivator(getModelGroup(), roleAgent, "takeAction");
		addActivator(agentAct);
		agentCollectExperience = new MethodActivator(getModelGroup(), roleAgent, "collectExperience");
		addActivator(agentCollectExperience);
		agentUpdatePolicy = new MethodActivator(getModelGroup(), roleAgent, "updatePolicy");
		addActivator(agentUpdatePolicy);
		agentEndEpisode = new MethodActivator(getModelGroup(), roleAgent, "endEpisode");
		addActivator(agentEndEpisode);
		viewers = new MethodActivator(getEngineGroup(), SimuOrganization.VIEWER_ROLE, "display");
		addActivator(viewers);

	}

	
	@Override
	public void onSimulationStart() {
		super.onSimulationStart();
		getLogger().info("Simulation has Started");
		initEnvironment.execute();
		env = (MLKEnvironment) getEnvironment();
	}

	/**
	 * This methods called the step method of the environment and updates the agents' policies.
	 * It also checks the criteria for ending an episode and starting or ending display.
	 */
	@Override
	public void doSimulationStep() {
		super.doSimulationStep();
		
		clearPreviousStepVariables.execute();
		
		computeObservations.execute();
		
		agentAct.execute();
		
		step.execute();
		
		agentCollectExperience.execute();
		
		agentUpdatePolicy.execute(counter);
		
		handleEndEpisode();
		
		handleDisplay();
		viewers.execute();
		
		counter++;
		getCriteriaEndEpisode().update(Optional.of(env.getState()));
		
		handleEndSimulation();
	}
	
	/**
	 * Handles the logic for displaying the simulation based on the criteria for starting and ending the display.
	 */
	protected void handleDisplay() {
		if (getCriteriaStartDisplay().isMet()) {
			setPause(getPauseDisplayValue());
		}
		else if(getCriteriaEndDisplay().isMet()) {
			setPause(0);
		}
	}
	
	/**
	 * Handles the logic for ending an episode based on the criterion for ending an episode.
	 * If the criterion is met, it calls the episodeEnded method to update the criteria and reset the environment.
	 */
	protected void handleEndEpisode() {
		if (getCriteriaEndEpisode().isMet()) {
			episodeEnded();	
		}
	}
	
	/**
	 * Handles the logic for ending the simulation based on the criterion for ending the simulation.
	 */
	protected void handleEndSimulation() {
		if (getCriteriaEndSimulation().isMet()) {
			envEnd.execute();
			onEnd();
		}
	}
	
	/**
	 * This method is called when an episode ends. 
	 * <p>
	 * It updates the criteria for ending the simulation and start/stop display, 
	 * executes the agent's endEpisode method, resets the environment, and logs the end of the episode.
	 * </p>
	 */
	protected void episodeEnded() {
		updateOnEndEpisode(Optional.of(env.getState()));
		envEndEpisode.execute();
		agentEndEpisode.execute();
		reset.execute();
		getCriteriaEndEpisode().reset();
		getLogger().info("Episode ended");
	}
	
	/**
	 * This method updates the criteria when an episode ends.
	 * This includes updating the criteria for ending the simulation and starting or ending the display.
	 * @param state
	 */
	protected void updateOnEndEpisode(Optional<State> state) {
		getCriteriaEndSimulation().update(state);
		getCriteriaStartDisplay().update(state);
		getCriteriaEndDisplay().update(state);
	}
	
	/**
	 * Resets all scheduling criteria to their initial states.
	 */
	protected void resetCriteria() {
		getCriteriaEndEpisode().reset();
		getCriteriaStartDisplay().reset();
		getCriteriaEndDisplay().reset();
		getCriteriaEndSimulation().reset();
	}
	/**
	 * Returns the criterion for ending an episode.
	 * 
	 * @return The criterion for ending an episode.
	 */
	public abstract Criterion getCriteriaEndEpisode();
	
	/**
	 * Returns the criterion for starting a display.
	 * 
	 * @return The criterion for starting a display.
	 */
	public abstract Criterion getCriteriaStartDisplay();

	/**
	 * Returns the criterion for ending a display.
	 * 
	 * @return The criterion for ending a display.
	 */
	public abstract Criterion getCriteriaEndDisplay();

	/**
	 * Returns the criterion for ending the simulation.
	 *
	 * @return The criterion for ending the simulation.
	 */
	public abstract Criterion getCriteriaEndSimulation();

	/**
	 * Returns the pause in milliseconds for the display.
	 * This value is used to control the speed of the display updates.
	 * @return 50 milliseconds by default.
	 */
	protected int getPauseDisplayValue() {
		return 50;
	}
	
}
