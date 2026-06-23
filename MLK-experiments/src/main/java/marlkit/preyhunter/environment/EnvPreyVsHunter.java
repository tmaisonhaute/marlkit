package marlkit.preyhunter.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2DDouble;
import environment.EnvironmentStandard;
import environment.state.State;
import marlkit.preyhunter.agent.HunterAgent;
import marlkit.preyhunter.agent.PreyAgent;
import marlkit.preyhunter.environment.events.HunterHunterDistanceEvent;
import marlkit.preyhunter.environment.events.HunterPreyDistanceEvent;
import marlkit.preyhunter.environment.events.PreyCatchEvent;
import reward.ReactionEvent;
import reward.RewardModel;
import rewardmodelimplementation.MixedReward;
import util.Pair;

/**
 * Continuous 2D Prey vs Hunter environment.
 *
 * Hunters try to catch preys in a continuous 2D space.
 * The state is represented by {@link StatePreyHunter2D}.
 */

public class EnvPreyVsHunter extends EnvironmentStandard {

    protected StatePreyHunter2D state;

    private final double captureRadius;
    private final double hunterViewRange;
    private final double preyViewRange;
    protected final int requiredHuntersToCapture;

    /**
     * Default constructor required by some MadKit launch modes.
     */
    public EnvPreyVsHunter() {
        this(10, 10, 1.0, Double.POSITIVE_INFINITY, 2.0, new MixedReward(), 1);
    }

    public EnvPreyVsHunter(int width, int height, double captureRadius, double hunterViewRange, double preyViewRange,
            RewardModel rewardModel, int requiredHuntersToCapture) {
        super(width, height, rewardModel);

        if (captureRadius < 0.0) {
            throw new IllegalArgumentException("captureRadius must be >= 0.");
        }
        if (hunterViewRange < 0.0) {
            throw new IllegalArgumentException("hunterViewRange must be >= 0.");
        }
        if (preyViewRange < 0.0) {
            throw new IllegalArgumentException("preyViewRange must be >= 0.");
        }

        this.captureRadius = captureRadius;
        this.hunterViewRange = hunterViewRange;
        this.preyViewRange = preyViewRange;
        this.requiredHuntersToCapture = requiredHuntersToCapture;
        
    }


    /**
     * Initializes the continuous state.
     */
    @Override
    protected void onActivation() {
        super.onActivation();
        initState();
        
    }
    

    protected void initState() {
        this.state = new StatePreyHunter2D(getWidth(), getHeight(), hunterViewRange, false, false, false, captureRadius);

        this.state.setPreyViewRange(preyViewRange);
    }


    /**
     * Setup state.
     *
     * For this environment, the state itself does not contain static objects.
     * Agent placement is handled in setupAgents().
     */
    @Override
    protected void setupState() {
        state.reset();
    }

    /**
     * Randomly places agents in the continuous 2D space.
     *
     * Preys are initially placed in the upper half.
     * Hunters are initially placed in the lower half.
     */
    @Override
    protected void setupAgents() {
        RandomGenerator rg = prng();

        for (MLKAgent agent : agents.getAgents()) {
            Pair<Double, Double> position = randomInitialPosition(agent, rg);
            state.addAgent(agent, position);
			if (agent instanceof PreyAgent prey) {
				prey.setCaptured(false);
			}
        }
    }

    /**
     * Registers an agent in the environment.
     *
     * @param agent agent to add
     */
    @Override
    public void addAgent(MLKAgent agent) {
        agents.addAgent(agent);
    }

    /**
     * Resets the environment.
     */
    @Override
    public void reset() {
        setupState();
        setupAgents();
    }

    /**
     * Applies agent actions and computes reaction events.
     *
     * @param actions actions chosen by agents
     * @return reaction events per agent
     */
    @Override
    public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
        Map<MLKAgent, List<ReactionEvent>> results = initEmptyResults();

        moveAllAgents(actions);

        applyHunterHunterDistancePenalty(results);
        applyHunterPreyEvents(results);

        return results;
    }

    /**
     * Initializes an empty event list for every registered agent.
     *
     * @return empty results map
     */
    private Map<MLKAgent, List<ReactionEvent>> initEmptyResults() {
        Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();

        for (MLKAgent agent : agents.getAgents()) {
            results.put(agent, new ArrayList<>());
        }

        return results;
    }

    /**
     * Moves all agents according to their actions.
     *
     * @param actions actions by agent
     */
    private void moveAllAgents(Map<MLKAgent, Action> actions) {
        for (MLKAgent agent : agents.getAgents()) {
            Action action = actions.get(agent);

            if (action == null) {
                continue;
            }

            if (!(action instanceof Move2DDouble move)) {
                throw new IllegalArgumentException(
                        "EnvPreyVsHunter expects Move2DDouble actions, got: "
                                + action.getClass().getSimpleName()
                );
            }

            state.moveAgent(agent, move.getValue());
        }
    }

    /**
     * Applies hunter-hunter distance penalty to hunters.
     *
     * @param results event map to update
     */
    private void applyHunterHunterDistancePenalty(Map<MLKAgent, List<ReactionEvent>> results) {

        List<HunterAgent> hunters = state.getHunterAgents();
        for (int i = 0; i < hunters.size(); i++) {
        	HunterAgent hunter1 = hunters.get(i);
        	
            for (int j = i + 1; j < hunters.size(); j++) {
                HunterAgent hunter2 = hunters.get(j);

                double distance = state.distance(hunter1, hunter2);
                HunterHunterDistanceEvent hhevent = new HunterHunterDistanceEvent(distance);
                
                results.get(hunter1).add(hhevent);
                results.get(hunter2).add(hhevent);
            }
        }
    }

    /**
     * Applies hunter-prey distance penalties and catch events.
     *
     * @param results event map to update
     */
    private void applyHunterPreyEvents(Map<MLKAgent, List<ReactionEvent>> results) {
		for (PreyAgent prey : state.getPreyAgents()) {
		        List<HunterAgent> huntersInCaptureRange = new ArrayList<>();
		
		        for (HunterAgent hunter : state.getHunterAgents()) {
		            double distance = state.distance(hunter, prey);
		            results.get(hunter).add(new HunterPreyDistanceEvent(distance));
		
		            if (state.isPreyInCaptureRange(hunter, prey)) {
		                huntersInCaptureRange.add(hunter);
		            }
		        }
		
		        if (huntersInCaptureRange.size() >= this.requiredHuntersToCapture) {
		            for (HunterAgent hunter : huntersInCaptureRange) {
		                results.get(hunter).add(new PreyCatchEvent());
		            }
		            prey.setCaptured(true);
		        }
		    }
    }

    /**
     * Generates a random initial position for an agent.
     *
     * Preys are placed in the upper half.
     * Hunters are placed in the lower half.
     *
     * @param agent agent to place
     * @param rg random generator
     * @return random position
     */
    private Pair<Double, Double> randomInitialPosition(MLKAgent agent, RandomGenerator rg) {
        double x = rg.nextDouble() * getWidth();
        double y = rg.nextDouble() * (getHeight() / 2.0);

        if (agent instanceof HunterAgent) {
            y += getHeight() / 2.0;
        }

        return new Pair<>(x, y);
    }

    /**
     * Computes Euclidean distance between two agents.
     *
     * @param ag1 first agent
     * @param ag2 second agent
     * @return distance
     */
    public double distance2D(MLKAgent ag1, MLKAgent ag2) {
        return state.distance(ag1, ag2);
    }

    /**
     * Returns all current agent positions.
     *
     * @return positions map
     */
    public Map<MLKAgent, Pair<Double, Double>> getAgentsPositions() {
        return state.getAgentsPositions();
    }

    /**
     * Returns prey positions.
     *
     * @return prey positions
     */
    public Map<MLKAgent, Pair<Double, Double>> getPreysPositions() {
        return state.getPreysPositions();
    }

    /**
     * Returns hunter positions.
     *
     * @return hunter positions
     */
    public Map<MLKAgent, Pair<Double, Double>> getHuntersPositions() {
        return state.getHuntersPositions();
    }

    /**
     * Returns the current state.
     *
     * @return environment state
     */
    @Override
    public State getState() {
        return state;
    }
    

    public double getCaptureRadius() {
        return captureRadius;
    }

    public double getHunterViewRange() {
        return hunterViewRange;
    }

    public double getPreyViewRange() {
        return preyViewRange;
    }

}