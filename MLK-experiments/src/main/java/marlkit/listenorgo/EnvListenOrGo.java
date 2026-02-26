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
import rewardmodeling.ReactionEvent;
import rewardmodels.MixedReward;
import util.Pair;

public class EnvListenOrGo extends EnvironmentStandard {
    private static final double LISTEN_ACCURACY = 0.7; 
    
    private Choice correctChoice;
    private final Map<MLKAgent, Choice> agentsChoice; 
    private final Map<MLKAgent, ObservationListenOrGo> agentObservations;
    public EnvListenOrGo() {
        super(1, 1, new MixedReward());
        this.agentsChoice = new HashMap<>();
        this.agentObservations = new HashMap<>();
    }
    
    @Override
	protected void onActivation() {
		super.onActivation();
		setupState();
	}

    @Override
    protected void setupState() {
        RandomGenerator rg = prng();
        correctChoice = rg.nextBoolean() ? Choice.RIGHT : Choice.LEFT;
        
    }
    
    @Override
    protected void setupAgents() {
    	for (MLKAgent agent : agents.getAgents()) {
    		agentObservations.put(agent, new ObservationListenOrGo());
            agentsChoice.put(agent, Choice.NONE);
    	}
    }

    
    @Override
    public void addAgent(MLKAgent agent) {
        agents.addAgent(agent);
        
    }
    
    @Override
    public void computeObservations() {
        Map<MLKAgent, Observation> observations = new HashMap<>();
        for (MLKAgent agent : agents.getAgents()) {
            observations.put(agent, agentObservations.get(agent));
        }
        setAgentsObservations(observations);
    }
    
    @Override
    public Map<MLKAgent, Pair<Action, List<ReactionEvent>>> dynamics(Map<MLKAgent, Action> actions) {
        Map<MLKAgent, Pair<Action, List<ReactionEvent>>> results = new HashMap<>();
        Map<MLKAgent, Choice> directionsChosen = new HashMap<>();

        for (MLKAgent agent : agents.getAgents()) {
            Action action = actions.get(agent);
            ReactionEvent event = null;
            
            if (agentsChoice.get(agent) != Choice.NONE) {
                results.put(agent, new Pair<>(action, new DoNothingEvent().toList()));
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
            results.put(agent, new Pair<>(action, event.toList()));
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
    
    public Choice getCorrectChoice() {
        return correctChoice;
    }
    
    public Map<MLKAgent, Choice> getAgentsChoice() {
        return new HashMap<>(agentsChoice);
    }

    
    
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
