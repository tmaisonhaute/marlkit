package marlkit.launcher;

import simulation.LauncherMetadata;
import simulation.MLKLauncher;

/** Describes one executable launcher discovered from source metadata. */
public record LauncherDescriptor(
		Class<? extends MLKLauncher> launcherClass,
		LauncherMetadata metadata) {
	public String title() {
		return metadata.title();
	}

	public String documentationAnchor() {
		return metadata.documentationAnchor();
	}

	@Override
	public String toString() {
		return title();
	}
}
