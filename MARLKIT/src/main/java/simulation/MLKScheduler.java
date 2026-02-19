package simulation;

import java.util.Optional;
import java.util.logging.Level;

import environment.MLKEnvironment;
import environment.state.State;
import madkit.kernel.Activator;
import madkit.simulation.SimuOrganization;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;
import madkit.simulation.scheduler.MethodActivator;
import madkit.simulation.scheduler.TickBasedScheduler;
import util.criteria.Criterion;

/**
 * Scheduler for MARLKIT simulations that coordinates agent-environment interactions.
 * Manages the execution cycle of observations, actions, learning, and episode boundaries.
 */
public abstract class MLKScheduler extends TickBasedScheduler {
	private MLKEnvironment env;
	
	private Activator agentUpdatePolicy;
	private Activator agentEndEpisode;

	private Activator initEnvironment;
	private Activator step;
	private Activator reset;
	private Activator atexit;

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
		agentEndEpisode = new MethodActivator(getModelGroup(), roleAgent, "endEpisode");
		addActivator(agentEndEpisode);
		agentUpdatePolicy = new MethodActivator(getModelGroup(), roleAgent, "updatePolicy");
		addActivator(agentUpdatePolicy);
		
		initEnvironment = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "init");
		addActivator(initEnvironment);
		step = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "step");
		addActivator(step);
		reset = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "reset");
		addActivator(reset);
		atexit = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "onEnd");
		addActivator(atexit);
		viewers = new MethodActivator(getEngineGroup(), SimuOrganization.VIEWER_ROLE, "display");
		addActivator(viewers);

	}

	
	@Override
	public void onSimulationStart() {
		super.onSimulationStart();
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
		step.execute();
		agentUpdatePolicy.execute(counter);
		if (getCriteriaEndEpisode().isMet()) {
			updateOnEndEpisode(Optional.of(env.getState()));
			agentEndEpisode.execute();
			reset.execute();
			getCriteriaEndEpisode().reset();
		}
		if (getCriteriaStartDisplay().isMet()) {
			setPause(getPauseDisplayValue());
		}
		else if(getCriteriaEndDisplay().isMet()) {
			setPause(0);
		}
		counter++;
		getCriteriaEndEpisode().update(Optional.of(env.getState()));
		viewers.execute();
		if (getCriteriaEndSimulation().isMet()) {
			atexit.execute();
			onEnd();
		}
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
