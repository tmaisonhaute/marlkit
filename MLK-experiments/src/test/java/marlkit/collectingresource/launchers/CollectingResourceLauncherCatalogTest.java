package marlkit.collectingresource.launchers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import org.testng.annotations.Test;

import marlkit.launcher.ExperimentLauncherCatalog;
import marlkit.launcher.MarkdownDocumentation;

class CollectingResourceLauncherCatalogTest {

	@Test
	void discoversAllAnnotatedConcreteCollectingResourceLaunchers() {
		List<CollectingResourceLauncherCatalog.LauncherDescriptor> launchers = CollectingResourceLauncherCatalog.discover();

		assertThat(launchers)
				.extracting(CollectingResourceLauncherCatalog.LauncherDescriptor::launcherClass)
				.containsExactlyInAnyOrder(
						LauncherMixedReward.class,
						LauncherFullyCooperativeReward.class,
						LauncherFairMixedReward.class,
						LauncherLogisticalReward.class);
		assertThat(launchers)
				.extracting(CollectingResourceLauncherCatalog.LauncherDescriptor::documentationAnchor)
				.containsExactlyInAnyOrder(
						"mixed-reward",
						"fully-cooperative-reward",
						"fair-mixed-reward",
						"logistical-reward");
	}

	@Test
	void buildsACommandForTheSelectedLauncher() {
		List<String> command = LauncherProcessStarter.buildCommand(LauncherMixedReward.class);

		assertThat(command).isNotEmpty();
		if (System.getProperty("jdk.module.path", "").isBlank()) {
			assertThat(command).containsExactly(
					command.getFirst(),
					"-cp",
					System.getProperty("java.class.path"),
					LauncherMixedReward.class.getName());
		} else {
			assertThat(command).containsSubsequence(
					"--module-path",
					System.getProperty("jdk.module.path"),
					"--add-modules",
					"ALL-MODULE-PATH",
					"-m",
					"marlkit.xp/" + LauncherMixedReward.class.getName());
		}
	}

	@Test
	void packagesTheCollectingResourceDocumentation() {
		assertThat(CollectingResourceLauncherMenu.class.getResource("/marlkit/collectingresource/README.md"))
				.isNotNull();
	}

	@Test
	void packagesMathJaxAndKeepsDisplayMathSyntax() throws IOException {
		assertThat(CollectingResourceLauncherMenu.class
				.getResource("/marlkit/collectingresource/mathjax/tex-svg.js"))
				.isNotNull();

		String readme = new String(CollectingResourceLauncherMenu.class
				.getResourceAsStream("/marlkit/collectingresource/README.md")
				.readAllBytes(), StandardCharsets.UTF_8);
		assertThat(readme).contains("$$", "$\\bar{r}_i$");
	}

	@Test
	void packagesFiguresReferencedByTheReadme() {
		assertThat(CollectingResourceLauncherMenu.class
				.getResource("/marlkit/collectingresource/figures/mixed_final_policy.png"))
				.isNotNull();
		assertThat(CollectingResourceLauncherMenu.class
				.getResource("/marlkit/collectingresource/figures/fullycoop_final_policy.png"))
				.isNotNull();
		assertThat(CollectingResourceLauncherMenu.class
				.getResource("/marlkit/collectingresource/figures/logistical_final_policy.png"))
				.isNotNull();
	}

	@Test
	void rendersMarkdownTablesAsHtmlTables() {
		String rendered = CollectingResourceLauncherMenu.renderMarkdown("""
				| Reward | Outcome |
				|---|---|
				| Mixed | Individual |
				""");

		assertThat(rendered).contains("<table>", "<thead>", "<tbody>", "<th>Reward</th>");
	}

	@Test
	void exposesReadmeHeadingsForIndependentDocumentationNavigation() {
		var sections = CollectingResourceLauncherMenu.documentationSections("""
				# Experiment
				## Overview
				### Fully Cooperative Reward
				""");

		assertThat(sections)
				.extracting(CollectingResourceLauncherMenu.DocumentationSection::anchor)
				.containsExactly("overview", "fully-cooperative-reward");
	}

	@Test
	void groupsLaunchersByPackageLevelExperimentMetadata() {
		var experiments = ExperimentLauncherCatalog.discover();

		var collectingResource = experiments.stream()
				.filter(experiment -> experiment.packageName().equals("marlkit.collectingresource.launchers"))
				.findFirst()
				.orElseThrow();
		assertThat(collectingResource).satisfies(experiment -> {
			assertThat(experiment.title()).isEqualTo("Collecting-resource experiments");
			assertThat(experiment.documentationResource())
					.isEqualTo("/marlkit/collectingresource/README.md");
			assertThat(experiment.launchers()).hasSize(4);
		});
	}

	@Test
	void genericProcessStarterUsesTheLaunchersRuntimeModule() {
		List<String> command = marlkit.launcher.LauncherProcessStarter.buildCommand(LauncherMixedReward.class);

		if (!System.getProperty("jdk.module.path", "").isBlank()) {
			assertThat(command).contains("-m", "marlkit.xp/" + LauncherMixedReward.class.getName());
		}
	}

	@Test
	void genericDocumentationServiceRendersTablesAndExtractsSections() {
		assertThat(MarkdownDocumentation.renderMarkdown("""
				| A | B |
				|---|---|
				| 1 | 2 |
				""")).contains("<table>", "<th>A</th>");
		assertThat(MarkdownDocumentation.sections("## Overview\n### Details\n"))
				.extracting(section -> section.anchor())
				.containsExactly("overview", "details");
	}
}
