package environment.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.Agent;
import environment.observation.*;
import util.*;

public class State2DGridInt extends State2DGrid<Integer> {
	private int[][] grid;
	
	/**
	 * Constructor of the class State2DGridInt
	 * 
	 * @param nbLines          the number of lines of the grid
	 * @param nbCols           the number of columns of the grid
	 * @param defaultValue     the default value of the grid
	 * @param agentViewRange   the range of the agent's view
	 * @param neumannNeighbors whether the neighbors are the neumann neighbors or
	 *                         the moore neighbors
	 * 
	 */
	public State2DGridInt(int nbLines, int nbCols, int defaultValue, int agentViewRange, boolean neumannNeighbors) {
		this.grid = new int[nbLines][nbCols];
		this.nbLines = nbLines;
		this.nbCols = nbCols;
		this.agentViewRange = agentViewRange;
		this.neumannNeighbors = neumannNeighbors;
	}
	public State2DGridInt(int nbLines, int nbCols) {
		this(nbLines, nbCols, 0, Integer.MAX_VALUE, true);
	}
	
	public void addAgent(Agent agent, int x, int y) {
		if (x >= nbLines || x < 0 || y >= nbCols || y < 0) {
			throw new ArrayIndexOutOfBoundsException("L'agent ne peut pas être placé en position (" + x + ";" + y + ")");
		}
		agentsPosition.put(agent, new Pair<Integer, Integer>(x, y));
	}
	
	@Override
	public Map<Agent, Observation> getObservations() {
		List<Agent> agents = new ArrayList<>(agentsPosition.keySet());
		Map<Agent, Observation> obsMap = new HashMap<>();
		
		for (Agent a : agents) {
			obsMap.put(a, ObservationAtCells(getNeighbors(a)));
		}
		return obsMap;
		
	}

	@Override
	public Integer getValue(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(int row, int col, Integer value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
	}
	
	protected List<Cell> getNeighbors(Agent a){
		Pair<Integer, Integer> apos = this.agentsPosition.get(a);
		int ax = apos.getFirst();
		int ay = apos.getSecond();
		if (neumannNeighbors) {
			List<Cell> cells = new ArrayList<Cell>();
			int r = agentViewRange;
			for (int i = -r; i <= r; r++) {
				for(int j = -r + Math.abs(i); j <= r - Math.abs(i); j ++) {
					int x = i + ax;
					int y = j + ay;
					if (x >= 0 && x < nbLines && y >= 0 && y < nbCols) {
						cells.add(new Cell(x, y, grid[x][y]));	
					}
				}
			}
			return cells;
		}else {
			throw new Error("Not implemented yet");
		}
		
	}
	
	protected ObservationPositionsValues ObservationAtCells(List<Cell> cells) {
		ObservationPositionsValues obs = new ObservationPositionsValues();
		for(Cell c : cells) {
			Tuple position = new Tuple(Arrays.asList( (double) c.x, (double)c.y));
			obs.add(new ObservationPositionValue(position, c.val));
		}
		return obs;
		
	}
}

class Cell {
	int x;
	int y;
	int val;
	public Cell(int x, int y, int val) {
		this.x = x;
		this.y = y;
		this.val = val;
	}
	
}
