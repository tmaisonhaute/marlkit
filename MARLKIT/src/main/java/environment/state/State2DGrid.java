package environment.state;

import java.util.HashMap;
import java.util.Map;

import agent.MLKAgent;
import util.Pair;

/**
 * Abstract base class for 2D grid-based environment states.
 * Manages agent positions and provides methods for grid manipulation.
 *
 * @param <T> the type of values stored in the grid cells
 */
public abstract class State2DGrid<T> implements State{
	protected int width;
	protected int height;
	protected int agentViewRange;
	protected boolean neumannNeighbors;
	protected boolean observeAgentsPositions = false;
	protected Map<MLKAgent, Pair<Integer, Integer>> agentsPosition = new HashMap<>();
	
	/**
	 * Resets the grid to its initial state.
	 */
	public abstract void reset();
	
	/**
	 * Returns the value at the specified grid position.
	 *
	 * @param row the row index
	 * @param col the column index
	 * @return the value at the position
	 */
	public abstract T getValue(int row, int col);
	
	/**
	 * Returns the value at the specified position.
	 *
	 * @param position the grid position
	 * @return the value at the position
	 */
	public abstract T getValue(Pair<Integer, Integer> position);
	
	/**
	 * Sets the value at the specified grid position.
	 *
	 * @param row the row index
	 * @param col the column index
	 * @param value the value to set
	 */
    public abstract void setValue(int row, int col, T value);
    
    /**
	 * Sets the value at the specified position.
	 *
	 * @param position the grid position
	 * @param value the value to set
	 */
    public abstract void setValue(Pair<Integer, Integer> position, T value);
    
    public abstract void print();
    
    /**
	 * Returns a copy of all agent positions.
	 *
	 * @return a map of agents to their positions
	 */
    public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions(){
    	return new HashMap<>(agentsPosition);
    }
    
    /**
	 * Returns the position of a specific agent.
	 *
	 * @param agent the agent to locate
	 * @return the agent's position
	 */
    public Pair<Integer, Integer> getAgentPosition(MLKAgent agent){
    	return agentsPosition.get(agent);
    }
    
    /**
	 * Moves an agent by the specified delta, respecting grid boundaries.
	 *
	 * @param agent the agent to move
	 * @param move the movement delta (dx, dy)
	 */
    public void moveAgent(MLKAgent agent, Pair<Integer, Integer> move) {
    	Pair<Integer, Integer> position = agentsPosition.get(agent).clone();
    	int new_i = Math.max(0, Math.min(width - 1, position.getFirst() + move.getFirst()));
    	int new_j = Math.max(0, Math.min(height - 1, position.getSecond() + move.getSecond()));
		position.setFirst(new_i);
    	position.setSecond(new_j);
    	agentsPosition.put(agent, position);
    }

	/**
	 * Returns the width of the grid.
	 *
	 * @return the grid width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of the grid.
	 *
	 * @return the grid height
	 */
	public int getHeight() {
		return height;
	}
}

