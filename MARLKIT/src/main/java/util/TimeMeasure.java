package util;

import madkit.kernel.AgentLogger;

public final class TimeMeasure {
	private TimeMeasure() {
		throw new AssertionError("Utility class");
	}
	
	public static void logTime(AgentLogger logger, String message) {
		logger.info(System.nanoTime() + " - " + message);
	}

}
