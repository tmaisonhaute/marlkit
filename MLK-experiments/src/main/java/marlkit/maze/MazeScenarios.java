package marlkit.maze;

import util.Pair;

public final class MazeScenarios {
	private MazeScenarios() {
	}

	public static MazeScenario fromText(String... rows) {
		if (rows == null || rows.length == 0) {
			throw new IllegalArgumentException("rows must not be empty");
		}
		int width = rows.length;
		int height = rows[0].length();
		if (height == 0) {
			throw new IllegalArgumentException("rows must not be empty strings");
		}

		int[][] cells = new int[width][height];
		Pair<Integer, Integer> spawn = null;
		boolean hasExit = false;

		for (int i = 0; i < width; i++) {
			if (rows[i].length() != height) {
				throw new IllegalArgumentException("All rows must have the same length");
			}
			for (int j = 0; j < height; j++) {
				char c = rows[i].charAt(j);
				switch (c) {
				case '.':
					cells[i][j] = MazeCellType.FREE.getCode();
					break;
				case 'S':
					if (spawn != null) {
						throw new IllegalArgumentException("Only one SPAWN is allowed");
					}
					spawn = new Pair<>(i, j);
					cells[i][j] = MazeCellType.SPAWN.getCode();
					break;
				case 'H':
					cells[i][j] = MazeCellType.HOLE.getCode();
					break;
				case 'E':
					hasExit = true;
					cells[i][j] = MazeCellType.EXIT.getCode();
					break;
				case 'W':
					cells[i][j] = MazeCellType.WALL.getCode();
					break;
				default:
					throw new IllegalArgumentException("Unknown cell symbol: '" + c + "'");
				}
			}
		}

		if (spawn == null) {
			throw new IllegalArgumentException("A scenario must contain exactly one SPAWN (S)");
		}
		if (!hasExit) {
			throw new IllegalArgumentException("A scenario must contain at least one EXIT (E)");
		}

		return new MazeScenario(cells, spawn);
	}

	public static MazeScenario defaultMaze() {
		return fromText(
				"......",
				".W....",
				".W....",
				"EW.H.S",
				".W....",
				".W...."
		);
	}
}
