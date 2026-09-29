package marlkit.foraging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.testng.annotations.Test;

import marlkit.launcher.ExperimentLauncherCatalog;
import marlkit.launcher.MarkdownDocumentation;

class ForagingLauncherTest {
	@Test
	void discoversForagingAsASeparateDocumentedExperiment() {
		var experiment = ExperimentLauncherCatalog.discover().stream()
				.filter(candidate -> candidate.packageName().equals("marlkit.foraging.launchers"))
				.findFirst()
				.orElseThrow();

		assertThat(experiment.title()).isEqualTo("Foraging experiments");
		assertThat(experiment.documentationResource()).isEqualTo("/marlkit/foraging/README.md");
		assertThat(experiment.launchers())
				.extracting(launcher -> (Object) launcher.launcherClass())
				.containsExactlyInAnyOrder(
						marlkit.foraging.launchers.LauncherForagingFullyCooperative.class,
						marlkit.foraging.launchers.LauncherForagingMixedReward.class,
						marlkit.foraging.launchers.LauncherForagingFairMixedReward.class);
		assertThat(experiment.launchers())
				.extracting(launcher -> launcher.documentationAnchor())
				.containsExactlyInAnyOrder("fully-cooperative-reward", "mixed-reward", "fair-mixed-reward");
	}

	@Test
	void packagesForagingDocumentationAndFigures() throws IOException {
		assertThat(ForagingLauncherTest.class.getResource("/marlkit/foraging/README.md")).isNotNull();
		assertThat(ForagingLauncherTest.class
				.getResource("/marlkit/foraging/figures/foraging_totalReward.png"))
				.isNotNull();
		String readme = new String(ForagingLauncherTest.class
				.getResourceAsStream("/marlkit/foraging/README.md")
				.readAllBytes(), StandardCharsets.UTF_8);
		assertThat(readme).contains("### Fully cooperative reward", "### Fair mixed reward", "| Component |");
	}

	@Test
	void exposesForagingDocumentationAnchors() {
		assertThat(MarkdownDocumentation.sections(MarkdownDocumentation.readResource(
				ForagingLauncherTest.class, "/marlkit/foraging/README.md")))
				.extracting(section -> section.anchor())
				.contains("fully-cooperative-reward", "mixed-reward", "fair-mixed-reward");
	}
}
