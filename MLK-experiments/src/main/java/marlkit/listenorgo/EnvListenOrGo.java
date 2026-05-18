package marlkit.listenorgo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.EnvironmentStandard;
import environment.observation.Observation;
import environment.state.State;
import marlkit.listenorgo.events.DoNothingEvent;
import marlkit.listenorgo.events.ListenEvent;
import marlkit.listenorgo.events.MoveEvent;
import reward.ReactionEvent;
import rewardmodelimplementation.MixedReward;

/**
 * Environment for the ListenOrGo multi-agent cooperative task.
 * <p>
 * At the start of each episode, one of two doors ({@link Choice#LEFT} or
 * {@link Choice#RIGHT}) is randomly selected as the correct one. Agents may
 * repeatedly {@link ActionListen listen} for a noisy signal about the correct door,
 * or immediately move to a door. A correct move yields a large positive reward;
 * an incorrect move yields a large negative reward. Listening incurs a small
 * negative reward. The episode resets once all agents have committed to a direction.
 * </p>
 */
public class EnvListenOrGo extends EnvironmentStandard {
    /** Probability that a listen action returns the correct choice. */
    private static final double LISTEN_ACCURACY = 0.7; 
    
    /** The correct door choice for the current episode. */
    private Choice correctChoice;
    /** Maps each agent to its committed direction choice ({@link Choice#NONE} until committed). */
    private final Map<MLKAgent, Choice> agentsChoice; 
    /** Maps each agent to its accumulated observation for the current episode. */
    private final Map<MLKAgent, ObservationListenOrGo> agentObservations;

    /**
     * Creates a new ListenOrGo environment with a {@link rewardmodelimplementation.MixedReward} reward model.
     */
    public EnvListenOrGo() {
        super(1, 1, new MixedReward());
        this.agentsChoice = new HashMap<>();
        this.agentObservations = new HashMap<>();
    }
    
    /**
     * Initialises the environment on activation by invoking the parent
     * activation logic and setting up the initial episode state.
     */
    @Override
	protected void onActivation() {
		super.onActivation();
		setupState();
	}

    /**
     * Sets up the initial state for a new episode by randomly selecting
     * the correct door ({@link Choice#LEFT} or {@link Choice#RIGHT}).
     */
    @Override
    protected void setupState() {
        RandomGenerator rg = prng();
        correctChoice = rg.nextBoolean() ? Choice.RIGHT : Choice.LEFT;
        
    }
    
    /**
     * Initialises each agent's observation and sets its committed choice to
     * {@link Choice#NONE} at the start of each episode.
     */
    @Override
    protected void setupAgents() {
    	for (MLKAgent agent : agents.getAgents()) {
    		agentObservations.put(agent, new ObservationListenOrGo());
            agentsChoice.put(agent, Choice.NONE);
    	}
    }

    
    /**
     * Registers an agent in the environment.
     *
     * @param agent the agent to add.
     */
    @Override
    public void addAgent(MLKAgent agent) {
        agents.addAgent(agent);
        
    }
    
    /**
     * Computes and dispatches the current observation to each agent.
     * Each agent receives its own accumulated {@link ObservationListenOrGo}.
     */
    @Override
    public void computeObservations() {
        Map<MLKAgent, Observation> observations = new HashMap<>();
        for (MLKAgent agent : agents.getAgents()) {
            observations.put(agent, agentObservations.get(agent));
        }
        setAgentsObservations(observations);
    }
    
    /**
     * Applies the joint action of all agents and computes the resulting reaction events.
     * <p>
     * Agents that have already committed to a direction receive a {@link events.DoNothingEvent}.
     * An agent that listens receives a noisy signal and a {@link events.ListenEvent}.
     * An agent that moves receives a {@link events.MoveEvent} with a positive or negative reward
     * depending on whether it chose the correct door.
     * Once all agents have committed, the environment resets for the next episode.
     * </p>
     *
     * @param actions a map from each agent to its chosen action.
     * @return a map from each agent to its resulting reaction events.
     */
    @Override
    public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
        Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();
        Map<MLKAgent, Choice> directionsChosen = new HashMap<>();

        for (MLKAgent agent : agents.getAgents()) {
            Action action = actions.get(agent);
            ReactionEvent event = null;
            
            if (agentsChoice.get(agent) != Choice.NONE) {
                results.put(agent, new DoNothingEvent().toList());
                continue;
            }
            
            if (action instanceof ActionListen) {
            	agentListen(agent);
            	event = new ListenEvent();
                
            } else if (action instanceof ActionGoLeft || action instanceof ActionGoRight) {
            	Choice selectedChoice =  action instanceof ActionGoLeft ? Choice.LEFT : Choice.RIGHT;
                agentsChoice.put(agent, selectedChoice);
                directionsChosen.put(agent, selectedChoice);
                event = new MoveEvent(correctChoice == selectedChoice); 
            } 
            results.put(agent, event.toList());
        }
        
        for (MLKAgent observer : agents.getAgents()) {
            for (Map.Entry<MLKAgent, Choice> entry : directionsChosen.entrySet()) {
                if (!entry.getKey().equals(observer)) {
                    agentObservations.get(observer).addOtherDirection(entry.getValue());
                }
            }
        }
        
        boolean allCommitted = agentsChoice.values().stream()
            .allMatch(choice -> choice != Choice.NONE);
        
        if (allCommitted) {
            getLogger().info("All agents committed - resetting environment");
            reset();
        }
        
        return results;
    }
    
	private void agentListen(MLKAgent agent) {
		RandomGenerator rg = prng();
        Choice listenResult = rg.nextDouble() < LISTEN_ACCURACY ?
            correctChoice : reverseChoice(correctChoice);
        agentObservations.get(agent).addListenResult(listenResult);
	}
    
    /**
     * Returns the current state of the environment, which exposes the correct
     * door choice and each agent's observation.
     *
     * @return an anonymous {@link environment.state.State} implementation.
     */
    @Override
    public State getState() {
        return new State() {
            
            @Override
            public void print() {
                System.out.println("Correct direction: " + correctChoice);
            }

			@Override
			public Map<MLKAgent, Observation> getObservations() {
				return getAgentsObservations();
			}

			@Override
			public void reset() {
			}
        };
    }
    
    /**
     * Returns the correct door choice for the current episode.
     *
     * @return {@link Choice#LEFT} or {@link Choice#RIGHT}.
     */
    public Choice getCorrectChoice() {
        return correctChoice;
    }
    
    /**
     * Returns a copy of the map associating each agent with its committed direction choice.
     * Agents that have not yet committed are mapped to {@link Choice#NONE}.
     *
     * @return a defensive copy of the agents-choice map.
     */
    public Map<MLKAgent, Choice> getAgentsChoice() {
        return new HashMap<>(agentsChoice);
    }

    
    
    /**
     * Returns the opposite door choice.
     *
     * @param choice the input choice.
     * @return {@link Choice#RIGHT} if {@code choice} is {@link Choice#LEFT},
     *         {@link Choice#LEFT} if {@code choice} is {@link Choice#RIGHT},
     *         or {@link Choice#NONE} otherwise.
     */
    protected Choice reverseChoice(Choice choice) {
        if (choice == Choice.LEFT) {
            return Choice.RIGHT;
        } else if (choice == Choice.RIGHT) {
            return Choice.LEFT;
        } else {
            return Choice.NONE;
        }
    }
}
