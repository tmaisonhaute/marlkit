package environment.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import util.Pair;
import util.Tuple;

public class State2DGridInt extends State2DGrid<Integer> {
	protected int[][] grid;
	
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
	public State2DGridInt(int nbLines, int nbCols, int agentViewRange, boolean neumannNeighbors) {
		this(nbLines, nbCols, agentViewRange, neumannNeighbors, false);
	}
	public State2DGridInt(int nbLines, int nbCols, int agentViewRange, boolean neumannNeighbors, boolean observeAgentsPositions) {
		this.grid = new int[nbLines][nbCols];
		this.width = nbLines;
		this.height = nbCols;
		this.agentViewRange = agentViewRange;
		this.neumannNeighbors = neumannNeighbors;
		this.observeAgentsPositions = observeAgentsPositions;
	}
	public State2DGridInt(int nbLines, int nbCols) {
		this(nbLines, nbCols, Integer.MAX_VALUE, true);
	}

	public State2DGridInt(int nbLines, int nbCols, boolean neumannNeighbors, boolean observeAgentsPositions) {
		this(nbLines, nbCols, Integer.MAX_VALUE, neumannNeighbors, observeAgentsPositions);
	}
	
	@Override
	public void reset() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j] = 0;
			}
		}
	}
	
	public void addAgent(MLKAgent agent, int x, int y) {
		if (x >= width || x < 0 || y >= height || y < 0) {
			throw new ArrayIndexOutOfBoundsException("L'agent ne peut pas être placé en position (" + x + ";" + y + ")");
		}
		agentsPosition.put(agent, new Pair<>(x, y));
	}
	
	@Override
	public Map<MLKAgent, Observation> getObservations() {
		List<MLKAgent> agents = new ArrayList<>(agentsPosition.keySet());
		Map<MLKAgent, Observation> obsMap = new HashMap<>();
		
		for (MLKAgent a : agents) {
			obsMap.put(a, observationAtCells(getNeighbors(a), a));
		}
		return obsMap;
	}

	@Override
	public Integer getValue(int row, int col) {
		return grid[row][col];
	}
	@Override
	public Integer getValue(Pair<Integer, Integer> position) {
		return getValue(position.getFirst(), position.getSecond());
	}

	@Override
	public void setValue(int row, int col, Integer value) {
		grid[row][col] = value;
	}
	@Override
    public void setValue(Pair<Integer, Integer> position, Integer value) {
		setValue(position.getFirst(), position.getSecond(), value);
	}
	
	public void addValue(int row, int col, Integer addValue) {
		setValue(row, col, getValue(row, col) + addValue);
	}
	public void addValue(Pair<Integer, Integer> position, Integer addValue) {
		addValue(position.getFirst(), position.getSecond(), addValue);
	}

	@Override
	public void print() {
		String txt = "";
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				txt += grid[i][j] + "\t";
			}
			txt += "\n";
		}
		System.out.println(txt);
	}
	
	protected List<Cell> getNeighbors(MLKAgent a){
		Pair<Integer, Integer> apos = this.agentsPosition.get(a);
		int ax = apos.getFirst();
		int ay = apos.getSecond();
		if (neumannNeighbors) {
			List<Cell> cells = new ArrayList<>();
			int wLimit = Math.min(this.agentViewRange, getWidth());
			for (int i = - wLimit; i <= wLimit; i++) {
				int hLimit = Math.min(this.agentViewRange - Math.abs(i), getHeight());
				for(int j = -hLimit ; j <= hLimit; j ++) {
					int x = i + ax;
					int y = j + ay;
					if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
						cells.add(new Cell(x, y, grid[x][y]));	
					}
				}
			}
			return cells;
		}else {
			throw new Error("Not implemented yet");
		}
	}
	
	/**
	 * Identifies agents that are within the view range based on the already calculated visible cells
	 * 
	 * @param agent the agent observing
	 * @param visibleCells the list of cells already determined to be visible
	 * @return list of agents within those visible cells
	 */
	protected List<MLKAgent> getAgentsInViewRange(MLKAgent agent, List<Cell> visibleCells) {
		List<MLKAgent> agentsInRange = new ArrayList<>();
		
		List<Pair<Integer, Integer>> visiblePositions = new ArrayList<>();
		for (Cell cell : visibleCells) {
			visiblePositions.add(new Pair<>(cell.x, cell.y));
		}
		
		for (Map.Entry<MLKAgent, Pair<Integer, Integer>> entry : agentsPosition.entrySet()) {
			MLKAgent otherAgent = entry.getKey();
			if (otherAgent.equals(agent)) continue; 
			
			Pair<Integer, Integer> otherPos = entry.getValue();
			int x = otherPos.getFirst();
			int y = otherPos.getSecond();
			
			if (visiblePositions.contains(new Pair(x, y)) ) {
				agentsInRange.add(otherAgent);
			}
		}
		
		return agentsInRange;
	}
	
	protected ObservationPositionsValues observationAtCells(List<Cell> cells, MLKAgent agent) {
		ObservationPositionsValues observation = new ObservationPositionsValues();
		Pair<Integer, Integer> agentPos = agentsPosition.get(agent);
		int agentX = agentPos.getFirst();
		int agentY = agentPos.getSecond();

		// Add cell values to observation
		for (Cell c : cells) {
			if (c.val != 0) {
				Tuple relativePosition = new Tuple(Arrays.asList(
					(double) (c.x - agentX), 
					(double) (c.y - agentY)
				));
				observation.addObservationPosition(new ObservationPositionValue(relativePosition, c.val));
			}
		}
		
		// If configured to observe agent positions
		if (observeAgentsPositions) {
			// Add current agent's absolute position
			Tuple currentAgentPosition = new Tuple(Arrays.asList((double) agentX, (double) agentY));
			observation.addObservationPosition(new ObservationPositionValue(currentAgentPosition, -1)); // -1 indicates current agent
			
			// Add other agents' relative positions - using our optimized method
			for (MLKAgent otherAgent : getAgentsInViewRange(agent, cells)) {
				Pair<Integer, Integer> otherAgentPos = agentsPosition.get(otherAgent);
				int otherX = otherAgentPos.getFirst();
				int otherY = otherAgentPos.getSecond();
				
				Tuple relativePosition = new Tuple(Arrays.asList(
					(double) (otherX - agentX), 
					(double) (otherY - agentY)
				));
				observation.addObservationPosition(new ObservationPositionValue(relativePosition, -2)); // -2 indicates another agent
			}
		}
		
		return observation;
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
