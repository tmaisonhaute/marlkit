package environment.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import util.Pair;

/**
 * Abstract base class for continuous 2D environment states.
 * 
 * This class manages:
 * - the size of the continuous 2D space,
 * - the position of agents,
 * - movement with boundary constraints,
 * - distance computations,
 * - optional visibility range logic.
 *
 */
public abstract class State2DSpacious implements State {

    protected double width;
    protected double height;

    protected double agentViewRange;

    protected boolean observeSelfPosition;
    protected boolean observeAgentsPositions;
    
    protected boolean toroidal = false;

    protected Map<MLKAgent, Pair<Double, Double>> agentsPosition;
    
    /**
     * Creates a continuous 2D state with infinite view range.
     * Agents view range is set to positive infinity, meaning they can observe all positions in the environment.
     * Agents cannot observe their own position or other agents' positions by default.
     * 
     * @param width width of the continuous space
     * @param height height of the continuous space
     */
	public State2DSpacious(double width, double height) {
		this(width, height, Double.POSITIVE_INFINITY);
	}

    /**
     * Creates a continuous 2D state.
     * Agents cannot observe their own position or other agents' positions by default.
     *
     * @param width width of the continuous space
     * @param height height of the continuous space
     * @param agentViewRange maximal distance an agent can observe
     */
    public State2DSpacious(double width, double height, double agentViewRange) {
        this(width, height, agentViewRange, false, false, false);
    }

    /**
     * Creates a continuous 2D state.
     *
     * @param width width of the continuous space
     * @param height height of the continuous space
     * @param agentViewRange maximal distance an agent can observe
     * @param observeSelfPosition whether agents can observe their own position
     * @param observeAgentsPositions whether agents can observe other agents' positions
     */
    public State2DSpacious(
            double width,
            double height,
            double agentViewRange,
            boolean observeSelfPosition,
            boolean observeAgentsPositions,
            boolean toroidal
    ) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("The space dimensions must be strictly positive.");
        }

        if (agentViewRange < 0) {
            throw new IllegalArgumentException("The agent view range must be positive or zero.");
        }

        this.width = width;
        this.height = height;
        this.agentViewRange = agentViewRange;
        this.observeSelfPosition = observeSelfPosition;
        this.observeAgentsPositions = observeAgentsPositions;
        this.toroidal = toroidal;
        this.agentsPosition = new HashMap<>();
    }

    /**
     * Adds an agent at the specified continuous position.
     *
     * @param agent the agent to add
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void addAgent(MLKAgent agent, double x, double y) {
        checkPositionInsideBounds(x, y);
        agentsPosition.put(agent, new Pair<>(x, y));
    }

    /**
     * Adds an agent at the specified continuous position.
     *
     * @param agent the agent to add
     * @param position the agent position
     */
    public void addAgent(MLKAgent agent, Pair<Double, Double> position) {
        addAgent(agent, position.getFirst(), position.getSecond());
    }

    /**
     * Removes an agent from the state.
     *
     * @param agent the agent to remove
     */
    public void removeAgent(MLKAgent agent) {
        agentsPosition.remove(agent);
    }

    /**
     * Returns a copy of all agent positions.
     *
     * @return a map of agents to their positions
     */
    public Map<MLKAgent, Pair<Double, Double>> getAgentsPositions() {
        return new HashMap<>(agentsPosition);
    }

    /**
     * Returns the position of a specific agent.
     *
     * @param agent the agent to locate
     * @return the agent position, or null if the agent is not registered
     */
    public Pair<Double, Double> getAgentPosition(MLKAgent agent) {
        Pair<Double, Double> position = agentsPosition.get(agent);

        if (position == null) {
            return null;
        }

        return position.clone();
    }

    /**
     * Sets the position of an existing agent.
     *
     * @param agent the agent
     * @param x new x-coordinate
     * @param y new y-coordinate
     */
    public void setAgentPosition(MLKAgent agent, double x, double y) {
        if (!agentsPosition.containsKey(agent)) {
            throw new IllegalArgumentException("The agent is not registered in the state.");
        }

        checkPositionInsideBounds(x, y);
        agentsPosition.put(agent, new Pair<>(x, y));
    }

    /**
     * Moves an agent by the specified continuous delta.
     * The final position is clamped inside the environment bounds.
     *
     * @param agent the agent to move
     * @param move movement vector
     */
    public void moveAgent(MLKAgent agent, Pair<Double, Double> move) {
        Pair<Double, Double> position = agentsPosition.get(agent);

        if (position == null) {
            throw new IllegalArgumentException("The agent is not registered in the state.");
        }

        double newX = position.getFirst() + move.getFirst();
        double newY = position.getSecond() + move.getSecond();
        
        if(toroidal) {
        	newX = newX % width;
            newY = newY % height; 
        } else {
	        newX = Math.clamp(newX, 0.0, width);
	        newY = Math.clamp(newY, 0.0, height);
        }
        agentsPosition.put(agent, new Pair<>(newX, newY));
    }

    /**
     * Moves an agent by the specified continuous delta.
     *
     * @param agent the agent to move
     * @param dx delta on x
     * @param dy delta on y
     */
    public void moveAgent(MLKAgent agent, double dx, double dy) {
        moveAgent(agent, new Pair<>(dx, dy));
    }

    /**
     * Computes the Euclidean distance between two positions.
     *
     * @param p1 first position
     * @param p2 second position
     * @return Euclidean distance
     */
    public double distance(Pair<Double, Double> p1, Pair<Double, Double> p2) {
        double dx = p1.getFirst() - p2.getFirst();
        double dy = p1.getSecond() - p2.getSecond();

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Computes the Euclidean distance between two agents.
     *
     * @param a1 first agent
     * @param a2 second agent
     * @return Euclidean distance between both agents
     */
    public double distance(MLKAgent a1, MLKAgent a2) {
        Pair<Double, Double> p1 = agentsPosition.get(a1);
        Pair<Double, Double> p2 = agentsPosition.get(a2);

        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Both agents must be registered in the state.");
        }

        return distance(p1, p2);
    }

    /**
     * Returns whether a position is in the view range of an agent.
     *
     * @param agent observing agent
     * @param position observed position
     * @return true if the position is visible
     */
    public boolean isInViewRange(MLKAgent agent, Pair<Double, Double> position) {
        Pair<Double, Double> agentPosition = agentsPosition.get(agent);

        if (agentPosition == null) {
            throw new IllegalArgumentException("The agent is not registered in the state.");
        }

        return distance(agentPosition, position) <= agentViewRange;
    }

    /**
     * Returns whether one agent is in the view range of another.
     *
     * @param observer observing agent
     * @param observed observed agent
     * @return true if observed is visible by observer
     */
    public boolean isAgentInViewRange(MLKAgent observer, MLKAgent observed) {
        if (observer.equals(observed)) {
            return true;
        }

        Pair<Double, Double> observedPosition = agentsPosition.get(observed);

        if (observedPosition == null) {
            throw new IllegalArgumentException("The observed agent is not registered in the state.");
        }

        return isInViewRange(observer, observedPosition);
    }
    
    public List<Pair<Double, Double>> getPointsInViewRange(MLKAgent agent, List<Pair<Double, Double>> points){
    	List<Pair<Double, Double>> visiblePoints = new ArrayList<>();
    	
		for (Pair<Double, Double> point : points) {
			if (isInViewRange(agent, point)) {
				visiblePoints.add(point.clone());
			}
		}
    	
    	return visiblePoints;
    }

    /**
     * Returns all agents visible by a given agent.
     *
     * @param observer observing agent
     * @return visible agents
     */
    public Map<MLKAgent, Pair<Double, Double>> getAgentsInViewRange(MLKAgent observer) {
        Map<MLKAgent, Pair<Double, Double>> visibleAgents = new HashMap<>();

        for (Map.Entry<MLKAgent, Pair<Double, Double>> entry : agentsPosition.entrySet()) {
            MLKAgent otherAgent = entry.getKey();

            if (otherAgent.equals(observer)) {
                continue;
            }

            if (isAgentInViewRange(observer, otherAgent)) {
                visibleAgents.put(otherAgent, entry.getValue().clone());
            }
        }

        return visibleAgents;
    }

    /**
     * Checks whether a position is inside the continuous space.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if the position is inside bounds
     */
    public boolean isInsideBounds(double x, double y) {
        return x >= 0.0 && x <= width && y >= 0.0 && y <= height;
    }

    /**
     * Checks whether a position is inside the continuous space.
     *
     * @param position position to check
     * @return true if the position is inside bounds
     */
    public boolean isInsideBounds(Pair<Double, Double> position) {
        return isInsideBounds(position.getFirst(), position.getSecond());
    }

    /**
     * Throws an exception if the position is outside bounds.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    protected void checkPositionInsideBounds(double x, double y) {
        if (!isInsideBounds(x, y)) {
            throw new IllegalArgumentException(
                    "Position outside bounds: (" + x + "; " + y + ")"
            );
        }
    }

    /**
     * Returns the width of the continuous space.
     *
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the continuous space.
     *
     * @return height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the agent view range.
     *
     * @return agent view range
     */
    public double getAgentViewRange() {
        return agentViewRange;
    }

    /**
     * Returns whether agents' positions are observable.
     *
     * @return true if agents' positions are observable
     */
    public boolean observesAgentsPositions() {
        return observeAgentsPositions;
    }
    
    /**
     * Returns whether agents can observe their own position.
     * @param observeSelfPosition
     */
    public void setObserveSelfPosition(boolean observeSelfPosition) {
    	this.observeSelfPosition = observeSelfPosition;
    }

    /**
     * Sets whether agents can observe other agents' positions.
     * @param observeAgentsPositions
     */
	public void setObserveAgentsPositions(boolean observeAgentsPositions) {
		this.observeAgentsPositions = observeAgentsPositions;
	}
	
	/**
	 * Returns whether the environment is toroidal (wrap-around).
	 * @param toroidal
	 */
	public void setToroidal(boolean toroidal) {
		this.toroidal = toroidal;
	}
}