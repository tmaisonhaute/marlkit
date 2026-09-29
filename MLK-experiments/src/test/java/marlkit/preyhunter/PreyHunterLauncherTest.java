package marlkit.preyhunter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.testng.annotations.Test;

import marlkit.launcher.ExperimentLauncherCatalog;
import marlkit.launcher.MarkdownDocumentation;

class PreyHunterLauncherTest {
	@Test
	void discoversAllPreyHunterLaunchersAsOneDocumentedExperiment() {
		var experiment = ExperimentLauncherCatalog.discover().stream()
				.filter(candidate -> candidate.packageName().equals("marlkit.preyhunter.launchers"))
				.findFirst()
				.orElseThrow();

		assertThat(experiment.title()).isEqualTo("PreyHunter experiments");
		assertThat(experiment.documentationResource()).isEqualTo("/marlkit/preyhunter/README.md");
		assertThat(experiment.launchers()).hasSize(8);
		assertThat(experiment.launchers())
				.extracting(launcher -> (Object) launcher.launcherClass())
				.containsExactlyInAnyOrder(
						marlkit.preyhunter.launchers.LauncherPVHPPO.class,
						marlkit.preyhunter.launchers.LauncherPVHDDPG.class,
						marlkit.preyhunter.launchers.LauncherPVHMADDPG.class,
						marlkit.preyhunter.launchers.LauncherPVHPPOBroadcastObservation.class,
						marlkit.preyhunter.launchers.LauncherPVHPPOAveragedPolicyParameters.class,
						marlkit.preyhunter.launchers.LauncherPVHDDPGBroadcastObservation.class,
						marlkit.preyhunter.launchers.LauncherPVHMADDPGBroadcastObservation.class,
						marlkit.preyhunter.launchers.LauncherPVHPPOBroadcastObservationAndAveragedParameters.class);
	}

	@Test
	void packagesPreyHunterDocumentationAndFigures() throws IOException {
		assertThat(PreyHunterLauncherTest.class.getResource("/marlkit/preyhunter/README.md")).isNotNull();
		assertThat(PreyHunterLauncherTest.class
				.getResource("/marlkit/preyhunter/figures/preyhunter_ddpg_totalreward.png"))
				.isNotNull();
		String readme = new String(PreyHunterLauncherTest.class
			.getResourceAsStream("/marlkit/preyhunter/README.md")
			.readAllBytes(), StandardCharsets.UTF_8);
		assertThat(readme).contains(
				"### PPO No Communication",
				"### MADDPG Centralized Critic",
				"| Component |");
	}

	@Test
	void exposesPreyHunterDocumentationAnchors() {
		assertThat(MarkdownDocumentation.sections(MarkdownDocumentation.readResource(
				PreyHunterLauncherTest.class, "/marlkit/preyhunter/README.md")))
				.extracting(section -> section.anchor())
				.contains("ppo-no-communication", "maddpg-centralized-critic",
						"ppo-broadcast-observation-and-averaged-parameters");
	}
}
