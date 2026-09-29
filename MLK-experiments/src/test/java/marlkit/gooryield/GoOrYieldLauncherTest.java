package marlkit.gooryield;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.testng.annotations.Test;

import marlkit.launcher.ExperimentLauncherCatalog;
import marlkit.launcher.MarkdownDocumentation;

class GoOrYieldLauncherTest {
	@Test
	void discoversAllGoOrYieldLaunchersAsOneDocumentedExperiment() {
		var experiment = ExperimentLauncherCatalog.discover().stream()
				.filter(candidate -> candidate.packageName().equals("marlkit.gooryield.launchers"))
				.findFirst()
				.orElseThrow();

		assertThat(experiment.title()).isEqualTo("Go or Yield experiments");
		assertThat(experiment.documentationResource()).isEqualTo("/marlkit/gooryield/README.md");
		assertThat(experiment.launchers())
				.extracting(launcher -> (Object) launcher.launcherClass())
				.containsExactlyInAnyOrder(
						marlkit.gooryield.launchers.LauncherScriptedVsIndependent.class,
						marlkit.gooryield.launchers.LauncherScriptedVsFrequencyPrediction.class,
						marlkit.gooryield.launchers.LauncherScriptedVsMiniMax.class,
						marlkit.gooryield.launchers.LauncherIndependent.class,
						marlkit.gooryield.launchers.LauncherMiniMax.class,
						marlkit.gooryield.launchers.LauncherFictitiousPlay.class);
		assertThat(experiment.launchers())
				.extracting(launcher -> launcher.documentationAnchor())
				.containsExactlyInAnyOrder(
						"scripted-opponent-vs-independent-agent",
						"scripted-opponent-vs-action-frequency-agent",
						"scripted-opponent-vs-minimax-agent",
						"two-independent-agents",
						"two-minimax-agents",
						"two-fictitious-play-agents");
	}

	@Test
	void packagesGoOrYieldDocumentationAndFigures() throws IOException {
		assertThat(GoOrYieldLauncherTest.class.getResource("/marlkit/gooryield/README.md")).isNotNull();
		assertThat(GoOrYieldLauncherTest.class
				.getResource("/marlkit/gooryield/figures/gooryield_gogo.png"))
				.isNotNull();
		String readme = new String(GoOrYieldLauncherTest.class
				.getResourceAsStream("/marlkit/gooryield/README.md")
				.readAllBytes(), StandardCharsets.UTF_8);
		assertThat(readme).contains(
				"### Scripted Opponent vs Independent Agent",
				"### Two Fictitious-Play Agents",
				"| Component |");
	}

	@Test
	void exposesGoOrYieldDocumentationAnchors() {
		assertThat(MarkdownDocumentation.sections(MarkdownDocumentation.readResource(
				GoOrYieldLauncherTest.class, "/marlkit/gooryield/README.md")))
				.extracting(section -> section.anchor())
				.contains("scripted-opponent-vs-independent-agent", "two-fictitious-play-agents");
	}
}
