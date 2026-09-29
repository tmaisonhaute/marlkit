package marlkit.launcher;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.classgraph.ClassGraph;
import simulation.ExperimentMetadata;
import simulation.LauncherMetadata;
import simulation.MLKLauncher;

/**
 * Discovers documented, executable experiments from runtime source metadata.
 * <p>
 * A package is included only when it has {@link ExperimentMetadata}; each
 * included launcher must be a concrete {@link MLKLauncher} with a public
 * {@code main(String[])} method and {@link LauncherMetadata}.
 */
public final class ExperimentLauncherCatalog {
	private static final String EXPERIMENT_ROOT_PACKAGE = "marlkit";

	private ExperimentLauncherCatalog() {
	}

	/**
	 * Finds all documented launchers below the MARLKIT experiment package.
	 *
	 * @return experiments sorted by metadata order and title
	 */
	public static List<ExperimentDescriptor> discover() {
		try (var scanResult = new ClassGraph()
				.enableClassInfo()
				.enableAnnotationInfo()
				.acceptPackages(EXPERIMENT_ROOT_PACKAGE)
				.scan()) {
			Map<String, ExperimentGroup> groups = new LinkedHashMap<>();
			for (Class<?> type : scanResult.getClassesWithAnnotation(LauncherMetadata.class).loadClasses()) {
				if (!MLKLauncher.class.isAssignableFrom(type)
						|| Modifier.isAbstract(type.getModifiers())
						|| !hasPublicMainMethod(type)) {
					continue;
				}
				ExperimentMetadata experimentMetadata = type.getPackage().getAnnotation(ExperimentMetadata.class);
				if (experimentMetadata == null || !hasResource(type, experimentMetadata.documentationResource())) {
					continue;
				}
				@SuppressWarnings("unchecked")
				Class<? extends MLKLauncher> launcherClass = (Class<? extends MLKLauncher>) type;
				LauncherDescriptor launcher = new LauncherDescriptor(
						launcherClass, type.getAnnotation(LauncherMetadata.class));
				groups.computeIfAbsent(type.getPackageName(), ignored ->
						new ExperimentGroup(experimentMetadata)).launchers.add(launcher);
			}
			return groups.entrySet().stream()
					.map(entry -> new ExperimentDescriptor(
							entry.getKey(), entry.getValue().metadata, entry.getValue().launchers.stream()
									.sorted(Comparator.comparing(LauncherDescriptor::title))
									.toList()))
					.sorted(Comparator.comparingInt((ExperimentDescriptor descriptor) -> descriptor.metadata().order())
							.thenComparing(ExperimentDescriptor::title))
					.toList();
		}
	}

	private static boolean hasPublicMainMethod(Class<?> type) {
		try {
			return Modifier.isStatic(type.getMethod("main", String[].class).getModifiers());
		} catch (NoSuchMethodException exception) {
			return false;
		}
	}

	private static boolean hasResource(Class<?> type, String resource) {
		return resource != null && type.getResource(resource) != null;
	}

	private static final class ExperimentGroup {
		private final ExperimentMetadata metadata;
		private final List<LauncherDescriptor> launchers = new java.util.ArrayList<>();

		private ExperimentGroup(ExperimentMetadata metadata) {
			this.metadata = metadata;
		}
	}
}
