package marlkit.preyhunter.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.state.State2DSpacious;
import marlkit.preyhunter.agent.HunterAgent;
import marlkit.preyhunter.agent.PreyAgent;
import util.Pair;
import util.Tuple;

/**
 * Continuous 2D state for the Prey-Hunter environment.
 *
 * This state stores hunter and prey positions in a continuous 2D space.
 * It also builds observations using relative positions.
 */
public class StatePreyHunter2D extends State2DSpacious {

    public static final double OBS_SELF = -1.0;
    public static final double OBS_PREY = 1.0;
    public static final double OBS_HUNTER = -2.0;

    private final List<PreyAgent> preyAgents;
    private final List<HunterAgent> hunterAgents;

    /**
     * Creates a PreyHunter continuous 2D state with infinite observation range.
     *
     * @param width environment width
     * @param height environment height
     */
    public StatePreyHunter2D(double width, double height) {
        this(width, height, Double.POSITIVE_INFINITY, false, true, false);
    }

    /**
     * Creates a PreyHunter continuous 2D state.
     *
     * @param width environment width
     * @param height environment height
     * @param agentViewRange agent view range
     */
    public StatePreyHunter2D(
            double width,
            double height,
            double agentViewRange
    ) {
        this(width, height, agentViewRange, false, true, false);
    }

    /**
     * Creates a fully configurable PreyHunter continuous 2D state.
     *
     * @param width environment width
     * @param height environment height
     * @param agentViewRange agent view range
     * @param observeSelfPosition whether agents observe themselves
     * @param observeAgentsPositions whether agents observe other agents
     * @param toroidal whether space is toroidal
     * @param agents all agents
     */
    public StatePreyHunter2D(
            double width,
            double height,
            double agentViewRange,
            boolean observeSelfPosition,
            boolean observeAgentsPositions,
            boolean toroidal
    ) {
        super(width, height, agentViewRange, observeSelfPosition, observeAgentsPositions, toroidal);

        this.preyAgents = new ArrayList<>();
        this.hunterAgents = new ArrayList<>();

    }


    @Override
    public void addAgent(MLKAgent agent, double x, double y) {
        super.addAgent(agent, x, y);
        registerTypedAgent(agent);
    }

    @Override
    public void addAgent(MLKAgent agent, Pair<Double, Double> position) {
        addAgent(agent, position.getFirst(), position.getSecond());
    }

    @Override
    public void removeAgent(MLKAgent agent) {
        super.removeAgent(agent);
        preyAgents.remove(agent);
        hunterAgents.remove(agent);
    }
    
    private void registerTypedAgent(MLKAgent agent) {
        if (agent instanceof PreyAgent prey && !preyAgents.contains(prey)) {
            preyAgents.add(prey);
        } else if (agent instanceof HunterAgent hunter && !hunterAgents.contains(hunter)) {
            hunterAgents.add(hunter);
        }
    }
    
    /**
     * Reset only positions registered in the state.
     *
     * Actual random placement is handled by the environment.
     */
    @Override
    public void reset() {
        agentsPosition.clear();
        preyAgents.clear();
        hunterAgents.clear();
    }

    /**
     * Returns observations for all agents.
     *
     * @return observation map
     */
    @Override
    public Map<MLKAgent, Observation> getObservations() {
        Map<MLKAgent, Observation> observations = new HashMap<>();

        for (MLKAgent agent : agentsPosition.keySet()) {
            observations.put(agent, buildObservation(agent));
        }

        return observations;
    }

    /**
     * Builds an observation for a given agent.
     *
     * Observation format:
     * - relative position of visible preys with value OBS_PREY
     * - relative position of visible hunters with value OBS_HUNTER
     * - optional self position with value OBS_SELF
     *
     * @param observer observing agent
     * @return observation
     */
    protected ObservationPositionsValues buildObservation(MLKAgent observer) {
        ObservationPositionsValues observation = new ObservationPositionsValues();

        Pair<Double, Double> observerPosition = getAgentPosition(observer);
        if (observerPosition == null) {
            return observation;
        }

        if (observeSelfPosition) {
            observation.addObservationPosition(
                    new ObservationPositionValue(
                            toTuple(observerPosition),
                            OBS_SELF
                    )
            );
        }

        if (!observeAgentsPositions) {
            return observation;
        }

        for (PreyAgent prey : preyAgents) {
            addAgentObservationIfVisible(observer, prey, OBS_PREY, observation);
        }

        for (HunterAgent hunter : hunterAgents) {
            addAgentObservationIfVisible(observer, hunter, OBS_HUNTER, observation);
        }

        return observation;
    }

    /**
     * Adds another agent to the observation if visible.
     *
     * @param observer observing agent
     * @param observed observed agent
     * @param value semantic value
     * @param observation observation to complete
     */
    private void addAgentObservationIfVisible(
            MLKAgent observer,
            MLKAgent observed,
            double value,
            ObservationPositionsValues observation
    ) {
        if (observer.equals(observed)) {
            return;
        }

        Pair<Double, Double> observedPosition = getAgentPosition(observed);

        if (observedPosition == null) {
            return;
        }

        if (!isInViewRange(observer, observedPosition)) {
            return;
        }

        Pair<Double, Double> relativePosition = relativePosition(observer, observed);

        observation.addObservationPosition(
                new ObservationPositionValue(
                        toTuple(relativePosition),
                        value
                )
        );
    }

    /**
     * Computes the relative position of observed from observer.
     *
     * @param observer observing agent
     * @param observed observed agent
     * @return relative position
     */
    public Pair<Double, Double> relativePosition(MLKAgent observer, MLKAgent observed) {
        Pair<Double, Double> observerPosition = getAgentPosition(observer);
        Pair<Double, Double> observedPosition = getAgentPosition(observed);

        if (observerPosition == null || observedPosition == null) {
            throw new IllegalArgumentException("Both agents must be registered in the state.");
        }

        return new Pair<>(
                observedPosition.getFirst() - observerPosition.getFirst(),
                observedPosition.getSecond() - observerPosition.getSecond()
        );
    }

    /**
     * Returns true if a hunter catches a prey according to a capture radius.
     *
     * @param hunter hunter agent
     * @param prey prey agent
     * @param captureRadius capture radius
     * @return true if prey is caught
     */
    public boolean isPreyCaught(HunterAgent hunter, PreyAgent prey, double captureRadius) {
        return distance(hunter, prey) <= captureRadius;
    }

    /**
     * Returns all hunter-prey distances.
     *
     * @return map of hunter-prey pairs to distances
     */
    public Map<Pair<HunterAgent, PreyAgent>, Double> getHunterPreyDistanceMap() {
        Map<Pair<HunterAgent, PreyAgent>, Double> distances = new HashMap<>();

        for (HunterAgent hunter : hunterAgents) {
            for (PreyAgent prey : preyAgents) {
                distances.put(new Pair<>(hunter, prey), distance(hunter, prey));
            }
        }

        return distances;
    }

    /**
     * Returns all hunter positions.
     *
     * @return hunter positions
     */
    public Map<MLKAgent, Pair<Double, Double>> getHuntersPositions() {
        Map<MLKAgent, Pair<Double, Double>> positions = new HashMap<>();

        for (HunterAgent hunter : hunterAgents) {
            Pair<Double, Double> position = getAgentPosition(hunter);
            if (position != null) {
                positions.put(hunter, position);
            }
        }

        return positions;
    }

    /**
     * Returns all prey positions.
     *
     * @return prey positions
     */
    public Map<MLKAgent, Pair<Double, Double>> getPreysPositions() {
        Map<MLKAgent, Pair<Double, Double>> positions = new HashMap<>();

        for (PreyAgent prey : preyAgents) {
            Pair<Double, Double> position = getAgentPosition(prey);
            if (position != null) {
                positions.put(prey, position);
            }
        }

        return positions;
    }

    /**
     * Returns prey agents.
     *
     * @return prey agents
     */
    public List<PreyAgent> getPreyAgents() {
        return preyAgents;
    }

    /**
     * Returns hunter agents.
     *
     * @return hunter agents
     */
    public List<HunterAgent> getHunterAgents() {
        return hunterAgents;
    }

    /**
     * Converts a Pair<Double, Double> into a Tuple.
     *
     * @param position position pair
     * @return tuple
     */
    private Tuple toTuple(Pair<Double, Double> position) {
        return new Tuple(List.of(position.getFirst(), position.getSecond()));
    }

    /**
     * Prints current state.
     */
    @Override
    public void print() {
        System.out.println("StatePreyHunter2D:");

        System.out.println("Hunters:");
        for (HunterAgent hunter : hunterAgents) {
            System.out.println("  " + hunter + " at " + getAgentPosition(hunter));
        }

        System.out.println("Preys:");
        for (PreyAgent prey : preyAgents) {
            System.out.println("  " + prey + " at " + getAgentPosition(prey));
        }
    }
}