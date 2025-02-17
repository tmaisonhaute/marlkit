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
		this.grid = new int[nbLines][nbCols];
		this.width = nbLines;
		this.height = nbCols;
		this.agentViewRange = agentViewRange;
		this.neumannNeighbors = neumannNeighbors;
	}
	public State2DGridInt(int nbLines, int nbCols) {
		this(nbLines, nbCols, Integer.MAX_VALUE, true);
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
		agentsPosition.put(agent, new Pair<Integer, Integer>(x, y));
	}
	
	@Override
	public Map<MLKAgent, Observation> getObservations() {
		List<MLKAgent> agents = new ArrayList<>(agentsPosition.keySet());
		Map<MLKAgent, Observation> obsMap = new HashMap<>();
		
		for (MLKAgent a : agents) {
			obsMap.put(a, ObservationAtCells(getNeighbors(a), a));
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
			List<Cell> cells = new ArrayList<Cell>();
			int r = agentViewRange;
			for (int i = -r; i <= r; r++) {
				for(int j = -r + Math.abs(i); j <= r - Math.abs(i); j ++) {
					int x = i + ax;
					int y = j + ay;
					if (x >= 0 && x < width && y >= 0 && y < height) {
						cells.add(new Cell(x, y, grid[x][y]));	
					}
				}
			}
			return cells;
		}else {
			throw new Error("Not implemented yet");
		}
	}
	
	protected ObservationPositionsValues ObservationAtCells(List<Cell> cells, MLKAgent agent) {
	    ObservationPositionsValues obs = new ObservationPositionsValues();
	    Pair<Integer, Integer> agentPos = agentsPosition.get(agent);
	    int agentX = agentPos.getFirst();
	    int agentY = agentPos.getSecond();

	    for (Cell c : cells) {
	        if (c.val != 0) {
	        	Tuple relativePosition = new Tuple(Arrays.asList((double) (c.x - agentX), (double) (c.y - agentY)));
	            obs.add(new ObservationPositionValue(relativePosition, c.val));
	        }
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
