package simulation;

import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;

import java.util.Optional;
import java.util.logging.Level;

import agent.MLKAgent;
import agent.communication.MLKAgentCommunicating;
import agent.modelofotheragent.MLKAgentModelingOthers;
import environment.MLKEnvironment;
import environment.state.State;
import evaluation.SystemEvaluator;
import madkit.kernel.Activator;
import madkit.simulation.SimuOrganization;
import madkit.simulation.scheduler.MethodActivator;
import madkit.simulation.scheduler.TickBasedScheduler;
import trainingexecutionstrategy.DecentralizedTrainingExecutionStrategy;
import trainingexecutionstrategy.TrainingExecutionStrategy;
import util.criteria.Criterion;

/**
 * Scheduler for MARLKIT simulations that coordinates agent-environment interactions.
 * Manages the execution cycle of observations, actions, learning, and episode boundaries.
 */
public abstract class MLKScheduler extends TickBasedScheduler {
	protected MLKEnvironment env;
	protected SchedulerCriteria criteriaModule;
	private SystemEvaluator systemEvaluator;
	private TrainingExecutionStrategy trainingExecutionStrategy = new DecentralizedTrainingExecutionStrategy();
	
	private Activator initEnvironment;
	private Activator computeObservations;
	private Activator envReaction;
	private Activator buildExperiences;
	private Activator reset;
	private Activator clearPreviousStepVariables;
	private Activator envEndEpisode;
	private Activator envEnd;

	private Activator agentsAct;
	
	private Activator agentsPreInfluenceCommunicate;
	private Activator agentsHandlePreInfluenceCommunication;
	private Activator agentsPostReactionCommunicate;
	private Activator agentsHandlePostReactionCommunication;
	private Activator agentsUpdateModelsOfOtherAgents;
	private Activator agentsEndEpisodeCommunicate;
	private Activator agentsHandleEndEpisodeCommunication;

	private MethodActivator viewers;
	
	private int counter = 0;
	private boolean isActivated = false;

	/**
	 * Initializes the scheduler and sets up activators for agent and environment actions.
	 */
	@Override
	protected void onActivation() {
		getLogger().setLevel(Level.INFO);
		super.onActivation();
		
		initEnvironment = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "init");
		addActivator(initEnvironment);
		computeObservations = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "computeObservations");
		addActivator(computeObservations);
		envReaction = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "step");
		addActivator(envReaction);
		buildExperiences = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "buildExperiences");
		addActivator(buildExperiences);
		clearPreviousStepVariables = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "clearStepVariables");
		addActivator(clearPreviousStepVariables);
		reset = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "reset");
		addActivator(reset);
		envEndEpisode = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "onEpisodeEnd");
		addActivator(envEndEpisode);
		envEnd = new MethodActivator(getModelGroup(), ENVIRONMENT_ROLE, "onEnd");
		addActivator(envEnd);

		agentsAct = new MethodActivator(getModelGroup(), MLKAgent.DEFAULT_AGENT_ROLE, "takeAction");
		addActivator(agentsAct);

		
		trainingExecutionStrategy.activate(getModelGroup());
		trainingExecutionStrategy.getActivators().forEach(this::addActivator);
		
		
		agentsPreInfluenceCommunicate = new MethodActivator(getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE, "communicatePreInfluence");
		addActivator(agentsPreInfluenceCommunicate);
		agentsHandlePreInfluenceCommunication = new MethodActivator(getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE, "handleCommunicationPreInfluence");
		addActivator(agentsHandlePreInfluenceCommunication);
		agentsPostReactionCommunicate = new MethodActivator(getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE, "communicatePostReaction");
		addActivator(agentsPostReactionCommunicate);
		agentsHandlePostReactionCommunication = new MethodActivator(getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE, "handleCommunicationPostReaction");
		addActivator(agentsHandlePostReactionCommunication);
		agentsEndEpisodeCommunicate = new MethodActivator(getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE, "communicateEndEpisode");
		addActivator(agentsEndEpisodeCommunicate);
		agentsHandleEndEpisodeCommunication = new MethodActivator(getModelGroup(), MLKAgentCommunicating.DEFAULT_AGENT_ROLE, "handleCommunicationEndEpisode");
		addActivator(agentsHandleEndEpisodeCommunication);
		
		agentsUpdateModelsOfOtherAgents = new MethodActivator(getModelGroup(), MLKAgentModelingOthers.DEFAULT_AGENT_ROLE, "updateModelsOfOtherAgents");
		addActivator(agentsUpdateModelsOfOtherAgents);
		
		viewers = new MethodActivator(getEngineGroup(), SimuOrganization.VIEWER_ROLE, "display");
		addActivator(viewers);
		
		isActivated = true;
	}

	public void setCriteriaModule(SchedulerCriteria criteriaModule) {
		this.criteriaModule = criteriaModule;
	}
	
	public SchedulerCriteria getCriteriaModule() {
		return criteriaModule;
	}

	public void setSystemEvaluator(SystemEvaluator systemEvaluator) {
		this.systemEvaluator = systemEvaluator;
	}
	
	public void setTrainingExecutionStrategy(TrainingExecutionStrategy trainingExecutionStrategy) {
		if (!isActivated) {
			this.trainingExecutionStrategy = trainingExecutionStrategy;
			return;
		}
		this.trainingExecutionStrategy.getActivators().forEach(this::removeActivator);
		this.trainingExecutionStrategy = trainingExecutionStrategy;
		this.trainingExecutionStrategy.activate(getModelGroup());
		this.trainingExecutionStrategy.getActivators().forEach(this::addActivator);
		
	}

	
	/**
	 * Called when the simulation starts.
	 * Initializes the environment and stores a reference to it.
	 */
	@Override
	public void onSimulationStart() {
		super.onSimulationStart();
		getLogger().info("Simulation has Started");
		initEnvironment.execute();
		env = (MLKEnvironment) getEnvironment();
		if (systemEvaluator != null) {
			env.setSystemEvaluator(systemEvaluator);
		}
		reset.execute(); //TODO Useful only because SimulationStart is called multiple times (madkit related issue).
	}

	/**
	 * Executes one full simulation step with the following cycle:
	 * <ol>
	 *   <li>Clear previous step variables in the environment</li>
	 *   <li>Environment computes observations for all agents</li>
	 *   <li>Agents communicate</li>
	 *   <li>Agents observe, select, and send their actions (influences) to the environment</li>
	 *   <li>Environment processes dynamics, computes rewards, and stores experiences</li>
	 *   <li>Agents collect their experiences from the environment</li>
	 *   <li>Agents update their policies based on accumulated experiences</li>
	 *   <li>Handle episode end, display, criteria updates, and simulation end</li>
	 * </ol>
	 */
	@Override
	public void doSimulationStep() {
		super.doSimulationStep();
		
		clearPreviousStepVariables();
		
		computeObservations();
		
		agentsPreInfluenceCommunicate();
		
		agentsMakeObservation();
		
		agentsHandlePreInfluenceCommunication();
		
		agentsAct();
		
		environmentReaction();
		
		getCriteriaEndEpisode().update(Optional.of(env.getState()));
		
		buildExperiences();
		
		agentsCollectExperience();
		
		agentsPostReactionCommunication();
		
		agentsPostReactionHandleCommunication();
		
		agentsUpdateModelsOfOtherAgents();
		
		agentsUpdatePolicy(counter);
		
		handleEndEpisode();
		
		
		handleDisplay();
		displayViewers();
		
		counter++;
		
		handleEndSimulation();
	}

	/**
	 * Clears environment variables that are specific to the previous simulation step.
	 */
	protected void clearPreviousStepVariables() {
		clearPreviousStepVariables.execute();
	}

	/**
	 * Triggers the environment observation computation phase.
	 */
	protected void computeObservations() {
		computeObservations.execute();
	}

	/**
	 * Triggers the optional communication phase for communicating agents.
	 */
	protected void agentsPreInfluenceCommunicate() {
		agentsPreInfluenceCommunicate.execute();
	}
	
	/**
	 *  observationTriggers the observation phase for all agents, where they register their observations based on the current environment state.
	 */
	protected void agentsMakeObservation() {
		trainingExecutionStrategy.agentsMakeObservation();
	}

	/**
	 * Triggers the communication handling phase for communicating agents, allowing them to process incoming messages and update their internal state accordingly.
	 */
	protected void agentsHandlePreInfluenceCommunication() {
		agentsHandlePreInfluenceCommunication.execute();
	}
	
	/**
	 * Triggers the action selection and action sending phase for all agents.
	 */
	protected void agentsAct() {
		agentsAct.execute();
	}

	/**
	 * Triggers the environment transition and reward computation phase.
	 */
	protected void environmentReaction() {
		envReaction.execute();
	}
	
	/**
	 * Triggers the experience building phase for the environment, where it constructs experiences for all agents based on their actions, observations, and rewards.
	 */
	protected void buildExperiences() {
		buildExperiences.execute(getCriteriaEndEpisode().isMet());
	}

	/**
	 * Triggers the experience collection phase for all agents.
	 */
	protected void agentsCollectExperience() {
		trainingExecutionStrategy.agentsCollectExperience();
	}
	
	/**
	 * Triggers the optional post-reaction communication phase for communicating
	 * agents.
	 */
	protected void agentsPostReactionCommunication() {
		agentsPostReactionCommunicate.execute();
	}
	
	/**
	 * Triggers the post-reaction communication handling phase for communicating
	 * agents, allowing them to process incoming messages after actions have been
	 * taken.
	 */
	protected void agentsPostReactionHandleCommunication() {
		agentsHandlePostReactionCommunication.execute();
	}
	
	/**
	 * Triggers the model update phase for agents that model other agents, allowing them to update their internal models based on observed actions.
	 */
	protected void agentsUpdateModelsOfOtherAgents() {
		agentsUpdateModelsOfOtherAgents.execute();
	}

	/**
	 * Triggers the policy update phase for all agents.
	 *
	 * @param simulationStep current simulation step index
	 */
	protected void agentsUpdatePolicy(int simulationStep) {
		trainingExecutionStrategy.agentsUpdatePolicy(simulationStep);
	}

	/**
	 * Triggers the viewer display refresh phase.
	 */
	protected void displayViewers() {
		viewers.execute();
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
		trainingExecutionStrategy.agentsEndEpisode();
		
		agentsEndEpisodeCommunicate.execute();
		agentsHandleEndEpisodeCommunication.execute();
		
		reset.execute();
		getCriteriaEndEpisode().reset();
		getLogger().info("Episode ended");
	}
	
	/**
	 * Updates the criteria when an episode ends.
	 * This includes updating the criteria for ending the simulation and starting or ending the display.
	 *
	 * @param state the current environment state, wrapped in an Optional
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
	public Criterion getCriteriaEndEpisode(){
		return criteriaModule.getCriteriaEndEpisode();
	}
	
	/**
	 * Returns the criterion for starting a display.
	 * 
	 * @return The criterion for starting a display.
	 */
	public Criterion getCriteriaStartDisplay(){
		return criteriaModule.getCriteriaStartDisplay();
	}

	/**
	 * Returns the criterion for ending a display.
	 * 
	 * @return The criterion for ending a display.
	 */
	public Criterion getCriteriaEndDisplay(){
		return criteriaModule.getCriteriaEndDisplay();
	}

	/**
	 * Returns the criterion for ending the simulation.
	 *
	 * @return The criterion for ending the simulation.
	 */
	public Criterion getCriteriaEndSimulation(){
		return criteriaModule.getCriteriaEndSimulation();
	}

	/**
	 * Returns the pause in milliseconds for the display.
	 * This value is used to control the speed of the display updates.
	 * @return
	 */
	protected int getPauseDisplayValue() {
		return criteriaModule.getPauseDisplayValue();
	}

	
}
