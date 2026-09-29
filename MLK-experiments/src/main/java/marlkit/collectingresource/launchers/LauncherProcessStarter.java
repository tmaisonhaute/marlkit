package marlkit.collectingresource.launchers;

import java.io.IOException;
import java.util.List;

import simulation.MLKLauncher;

/** Compatibility facade for the generic launcher process starter. */
@Deprecated(forRemoval = false)
public final class LauncherProcessStarter {
	private LauncherProcessStarter() {
	}

	public static Process start(Class<? extends MLKLauncher> launcherClass) throws IOException {
		return marlkit.launcher.LauncherProcessStarter.start(launcherClass);
	}

	public static List<String> buildCommand(Class<? extends MLKLauncher> launcherClass) {
		return marlkit.launcher.LauncherProcessStarter.buildCommand(launcherClass);
	}
}