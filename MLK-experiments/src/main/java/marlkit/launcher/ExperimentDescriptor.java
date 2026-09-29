package marlkit.launcher;

import java.util.List;

import simulation.ExperimentMetadata;

/** Describes one documented experiment and its executable launchers. */
public record ExperimentDescriptor(
		String packageName,
		ExperimentMetadata metadata,
		List<LauncherDescriptor> launchers) {
	public ExperimentDescriptor {
		launchers = List.copyOf(launchers);
	}

	public String title() {
		return metadata.title();
	}

	public String documentationResource() {
		return metadata.documentationResource();
	}

	@Override
	public String toString() {
		return title();
	}
}
