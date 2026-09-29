package marlkit.collectingresource.launchers;

import java.util.List;

import marlkit.launcher.ExperimentLauncherCatalog;
import simulation.LauncherMetadata;
import simulation.MLKLauncher;

/**
 * Compatibility facade for clients that still request collecting-resource
 * launchers directly. New launcher browsers should use
 * {@link ExperimentLauncherCatalog}.
 */
@Deprecated(forRemoval = false)
public final class CollectingResourceLauncherCatalog {
	private CollectingResourceLauncherCatalog() {
	}

	public static List<LauncherDescriptor> discover() {
		return ExperimentLauncherCatalog.discover().stream()
				.filter(experiment -> experiment.packageName().equals("marlkit.collectingresource.launchers"))
				.flatMap(experiment -> experiment.launchers().stream())
				.map(LauncherDescriptor::new)
				.toList();
	}

	public record LauncherDescriptor(marlkit.launcher.LauncherDescriptor delegate) {
		public Class<? extends MLKLauncher> launcherClass() {
			return delegate.launcherClass();
		}

		public String title() {
			return delegate.title();
		}

		public String documentationAnchor() {
			return delegate.documentationAnchor();
		}

		public LauncherMetadata metadata() {
			return delegate.metadata();
		}

		@Override
		public String toString() {
			return title();
		}
	}
}