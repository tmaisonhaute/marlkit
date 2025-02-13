package environment.state;

import java.util.HashMap;
import java.util.Map;

import agent.MLKAgent;
import util.Pair;

public abstract class State2DGrid<T> implements State{
	protected int nbLines;
	protected int nbCols;
	protected int agentViewRange;
	protected boolean neumannNeighbors;
	protected Map<MLKAgent, Pair<Integer, Integer>> agentsPosition = new HashMap<>();
	
	public abstract void reset();
	public abstract T getValue(int row, int col);
	public abstract T getValue(Pair<Integer, Integer> position);
    public abstract void setValue(int row, int col, T value);
    public abstract void setValue(Pair<Integer, Integer> position, T value);
    public abstract void print();
    public Map<MLKAgent, Pair<Integer, Integer>> getAgentsPositions(){
    	return new HashMap<>(agentsPosition);
    }
    public Pair<Integer, Integer> getAgentPosition(MLKAgent agent){
    	return agentsPosition.get(agent);
    }
    public void moveAgent(MLKAgent agent, Pair<Integer, Integer> move) {
    	Pair<Integer, Integer> position = agentsPosition.get(agent).clone();
    	int new_i = Math.max(0, Math.min(nbLines - 1, position.getFirst() + move.getFirst()));
    	int new_j = Math.max(0, Math.min(nbCols - 1, position.getSecond() + move.getSecond()));
		position.setFirst(new_i);
    	position.setSecond(new_j);
    	agentsPosition.put(agent, position);
    }

	public int getWidth() {
		return nbLines;
	}
	public int getHeight() {
		return nbCols;
	}
}

