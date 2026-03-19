package marlkit.maze;

import java.util.Objects;

import environment.state.State2DGridInt;
import util.Pair;

public class MazeScenario {
	private final int width;
	private final int height;
	private final int[][] cells;
	private final Pair<Integer, Integer> spawn;

	public MazeScenario(int[][] cells, Pair<Integer, Integer> spawn) {
		Objects.requireNonNull(cells, "cells must not be null");
		Objects.requireNonNull(spawn, "spawn must not be null");
		if (cells.length == 0 || cells[0].length == 0) {
			throw new IllegalArgumentException("cells must not be empty");
		}
		this.width = cells.length;
		this.height = cells[0].length;
		this.cells = new int[width][height];

		for (int i = 0; i < width; i++) {
			if (cells[i].length != height) {
				throw new IllegalArgumentException("All rows must have the same length");
			}
			for (int j = 0; j < height; j++) {
				this.cells[i][j] = cells[i][j];
			}
		}

		validateSpawn(spawn);
		this.spawn = spawn.clone();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Pair<Integer, Integer> getSpawn() {
		return spawn.clone();
	}

	public void applyTo(State2DGridInt state) {
		if (state.getWidth() != width || state.getHeight() != height) {
			throw new IllegalArgumentException("Scenario and environment dimensions mismatch");
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				state.setValue(i, j, cells[i][j]);
			}
		}
	}

	public MazeCellType getCellType(Pair<Integer, Integer> position) {
		return MazeCellType.fromCode(getCellCode(position));
	}

	public int getCellCode(Pair<Integer, Integer> position) {
		int x = position.getFirst();
		int y = position.getSecond();
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IllegalArgumentException("Position out of bounds: " + position);
		}
		return cells[x][y];
	}

	private void validateSpawn(Pair<Integer, Integer> spawnPos) {
		int x = spawnPos.getFirst();
		int y = spawnPos.getSecond();
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IllegalArgumentException("Spawn out of bounds");
		}
		int code = cells[x][y];
		if (code != MazeCellType.SPAWN.getCode()) {
			throw new IllegalArgumentException("Spawn position must point to a SPAWN cell");
		}
	}
}
