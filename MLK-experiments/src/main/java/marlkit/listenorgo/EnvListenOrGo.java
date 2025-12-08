package marlkit.listenorgo;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.interaction.IndependantLearning;
import environment.EnvironmentStandard;
import environment.observation.Observation;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import environment.state.State;
import util.Pair;

public class EnvListenOrGo extends EnvironmentStandard {
    
    private static final double REWARD_CORRECT_DIRECTION = 10.0;
    private static final double REWARD_WRONG_DIRECTION = -10.0;
    private static final double REWARD_LISTEN = -0.1;
    private static final double LISTEN_ACCURACY = 0.7; // 70% de chance de donner la bonne direction
    
    private Choice correctChoice;
    private final Map<MLKAgent, Choice> agentsChoice; 
    private final Map<MLKAgent, ObservationListenOrGo> agentObservations;
    public EnvListenOrGo() {
        super(1, 1, new IndependantLearning());
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
        // Détermine aléatoirement la bonne direction pour cet épisode
        RandomGenerator rg = prng();
        correctChoice = rg.nextBoolean() ? Choice.RIGHT : Choice.LEFT;
        
        // Réinitialise les observations et choix pour les agents existants
        for (MLKAgent agent : agents.getAgents()) {
            agentObservations.put(agent, new ObservationListenOrGo());
            agentsChoice.put(agent, Choice.NONE);
        }
    }
    
    @Override
    public void reset() {
    	getLogger().talk("\nfefygeeeeeeeeeeeeeeeeeeeeeeeeee ");
        setupState();
    }

    
    @Override
    public void setupAgent(MLKAgent agent) {
        agents.addAgent(agent);
        agentObservations.put(agent, new ObservationListenOrGo());
        agentsChoice.put(agent, Choice.NONE);
    }
    
    @Override
    public Map<MLKAgent, Observation> getObservation() {
        Map<MLKAgent, Observation> observations = new HashMap<>();
        for (MLKAgent agent : agents.getAgents()) {
            observations.put(agent, agentObservations.get(agent));
        }
        return observations;
    }
    
    @Override
    public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
        Map<MLKAgent, Pair<Action, Reward>> results = new HashMap<>();
        Map<MLKAgent, Choice> directionsChosen = new HashMap<>();

        // Traite les actions de chaque agent
        for (MLKAgent agent : agents.getAgents()) {
            Action action = actions.get(agent);
            Reward reward = new RewardStandard(0);
            
            // Si l'agent a déjà pris une direction, il ne fait rien
            if (agentsChoice.get(agent) != Choice.NONE) {
                results.put(agent, new Pair<>(action, reward));
                continue;
            }
            
            if (action instanceof ActionListen) {
                // L'agent écoute et reçoit une indication (possiblement fausse)
                RandomGenerator rg = prng();
                Choice listenResult = rg.nextDouble() < LISTEN_ACCURACY ?
                    correctChoice : reverseChoice(correctChoice);
                
                agentObservations.get(agent).addListenResult(listenResult);
                reward.setReward(REWARD_LISTEN);
                
            } else if (action instanceof ActionGoLeft) {
                // L'agent prend la direction gauche
                agentsChoice.put(agent, Choice.LEFT);
                directionsChosen.put(agent, Choice.LEFT);
                
                if (correctChoice == Choice.LEFT) {
                    reward.setReward(REWARD_CORRECT_DIRECTION);
                } else {
                    reward.setReward(REWARD_WRONG_DIRECTION);
                }
                
            } else if (action instanceof ActionGoRight) {
                // L'agent prend la direction droite
                agentsChoice.put(agent, Choice.RIGHT);
                directionsChosen.put(agent, Choice.RIGHT);
                
                if (correctChoice == Choice.RIGHT) {
                    reward.setReward(REWARD_CORRECT_DIRECTION);
                } else {
                    reward.setReward(REWARD_WRONG_DIRECTION);
                }
            }
            
            results.put(agent, new Pair<>(action, reward));
        }
        

        String txt = "";
        for (Choice c : agentsChoice.values()) {
            txt += c + " ;\t";
        }
        getLogger().talk("\ntxtfefe " + txt);
        
        // Mettre à jour les observations : chaque agent voit les directions prises par les autres
        for (MLKAgent observer : agents.getAgents()) {
            for (Map.Entry<MLKAgent, Choice> entry : directionsChosen.entrySet()) {
                if (!entry.getKey().equals(observer)) {
                    agentObservations.get(observer).addOtherDirection(entry.getValue());
                }
            }
        }
        
        // Vérifier si tous les agents ont pris une décision
        boolean allCommitted = agentsChoice.values().stream()
            .allMatch(choice -> choice != Choice.NONE);
        
        if (allCommitted) {
            getLogger().info("All agents committed - resetting environment");
            reset();
        }
        
        return results;
    }
    
    @Override
    protected State getState() {
        return new State() {
            @Override
            public Map<MLKAgent, Observation> getObservations() {
                return getObservation();
            }
            
            @Override
            public void print() {
                System.out.println("Correct direction: " + correctChoice);
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
