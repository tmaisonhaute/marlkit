package marlkit.launcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import simulation.MLKLauncher;

/** Starts any concrete MARLKIT launcher in a separate JVM. */
public final class LauncherProcessStarter {
	private LauncherProcessStarter() {
	}

	public static Process start(Class<? extends MLKLauncher> launcherClass) throws IOException {
		return new ProcessBuilder(buildCommand(launcherClass)).inheritIO().start();
	}

	/**
	 * Builds a JPMS-aware command using the launcher's own runtime module.
	 *
	 * @param launcherClass concrete launcher class to start
	 * @return command arguments ready for {@link ProcessBuilder}
	 */
	public static List<String> buildCommand(Class<? extends MLKLauncher> launcherClass) {
		List<String> command = new ArrayList<>();
		command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());

		String modulePath = System.getProperty("jdk.module.path", "");
		String moduleName = launcherClass.getModule().getName();
		if (!modulePath.isBlank() && moduleName != null) {
			command.add("--module-path");
			command.add(modulePath);
			command.add("--add-modules");
			command.add("ALL-MODULE-PATH");
			command.add("-m");
			command.add(moduleName + "/" + launcherClass.getName());
			return List.copyOf(command);
		}

		command.add("-cp");
		command.add(System.getProperty("java.class.path"));
		command.add(launcherClass.getName());
		return List.copyOf(command);
	}
}
