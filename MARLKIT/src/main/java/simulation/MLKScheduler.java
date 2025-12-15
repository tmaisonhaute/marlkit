package simulation;

import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.logging.Level;

import madkit.kernel.Activator;
import madkit.simulation.SimuOrganization;
import madkit.simulation.scheduler.MethodActivator;
import madkit.simulation.scheduler.TickBasedScheduler;
import util.criteria.Criterion;

public abstract class MLKScheduler extends TickBasedScheduler {
	
	private Activator agentSendInfo;
	private Activator agentUpdatePolicy;
	private Activator agentEndEpisode;

	private Activator step;
	private Activator reset;
	private Activator atexit;

	private MethodActivator viewers;
	
	private int counter = 0;

	/**
	 * Creates a new MLK Scheduler with the specified name.
	 * 
	 */
	@Override
	protected void onActivation() {
		getLogger().setLevel(Level.INFO);
		super.onActivation();
		final String roleAgent = "mlkagent";
		agentSendInfo = new MethodActivator(getModelGroup(), roleAgent, "sendInfo");
		addActivator(agentSendInfo);
		agentEndEpisode = new MethodActivator(getModelGroup(), roleAgent, "endEpisode");
		addActivator(agentEndEpisode);
		agentUpdatePolicy = new MethodActivator(getModelGroup(), roleAgent, "updatePolicy");
		addActivator(agentUpdatePolicy);
		
		
		step = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "step");
		addActivator(step);
		reset = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "reset");
		addActivator(reset);
		atexit = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "onEnd");
		addActivator(atexit);
		viewers = new MethodActivator(getEngineGroup(), SimuOrganization.VIEWER_ROLE, "display");
		addActivator(viewers);

	}

	/**
	 * This method is called by the agents to share information with the environment.
	 * It triggers the agentSendInfo activator to execute.
	 */
	public void agentShareInformation() {
		agentSendInfo.execute();
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
			getCriteriaEndSimulation().update(null);
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
		updateCriteria();
		viewers.execute();
		if (getCriteriaEndSimulation().isMet()) {
			atexit.execute();
			onEnd();
		}
	}

	/**
	 * This method updates the criteria for starting and ending episodes and displays.
	 */
	protected void updateCriteria() {
		getCriteriaEndEpisode().update(null);
		getCriteriaStartDisplay().update(null);
		getCriteriaEndDisplay().update(null);
	}
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
