package environment.state;

import java.util.Map;

import agent.Agent;
import util.Pair;

public abstract class State2DGrid<T> implements State{
	protected int nbLines;
	protected int nbCols;
	protected int agentViewRange;
	protected boolean neumannNeighbors;
	protected Map<Agent, Pair<Integer, Integer>> agentsPosition;
	
	public abstract T getValue(int row, int col);
    public abstract void setValue(int row, int col, T value);
    public abstract void print();
    
}

