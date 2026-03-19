package marlkit.maze;

public enum MazeCellType {
	FREE(0),
	SPAWN(1),
	HOLE(2),
	EXIT(3),
	WALL(4);

	private final int code;

	MazeCellType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static MazeCellType fromCode(int code) {
		for (MazeCellType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown maze cell code: " + code);
	}
}
