package marlkit.launcher;

import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import simulation.MLKLauncher;

/** Generic JavaFX browser for documented MARLKIT experiments and launchers. */
public final class ExperimentLauncherMenu extends Application {
	private WebEngine documentationEngine;
	private ComboBox<DocumentationSection> documentationNavigation;
	private String pendingAnchor;
	private Label status;

	@Override
	public void start(Stage stage) {
		List<ExperimentDescriptor> experiments = ExperimentLauncherCatalog.discover();
		if (experiments.isEmpty()) {
			throw new IllegalStateException("No documented experiment launchers were discovered.");
		}

		ComboBox<ExperimentDescriptor> experimentSelection = new ComboBox<>();
		experimentSelection.getItems().setAll(experiments);
		experimentSelection.setPromptText("Select an experiment...");
		experimentSelection.setMaxWidth(Double.MAX_VALUE);

		ListView<LauncherDescriptor> launcherList = new ListView<>();
		launcherList.setPrefWidth(240);
		launcherList.getSelectionModel().clearSelection();
		documentationNavigation = new ComboBox<>();
		documentationNavigation.setPromptText("Navigate documentation...");
		documentationNavigation.setMaxWidth(Double.MAX_VALUE);
		Button documentationButton = new Button("Go to section");
		documentationButton.setDisable(true);
		documentationButton.setMaxWidth(Double.MAX_VALUE);
		Button beginningButton = new Button("Go to beginning");
		beginningButton.setDisable(true);
		beginningButton.setMaxWidth(Double.MAX_VALUE);
		Button launchButton = new Button("Launch selected experiment");
		launchButton.setDisable(true);
		status = new Label("Select an experiment and launcher.");
		status.setWrapText(true);

		WebView documentationView = new WebView();
		documentationEngine = documentationView.getEngine();
		documentationEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
			beginningButton.setDisable(newState != State.SUCCEEDED);
			if (newState == State.SUCCEEDED && pendingAnchor != null) {
				typesetMathAndScrollTo(pendingAnchor);
			}
		});

		experimentSelection.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, experiment) -> {
			pendingAnchor = null;
			launcherList.getItems().setAll(experiment == null ? List.of() : experiment.launchers());
			launcherList.getSelectionModel().clearSelection();
			documentationNavigation.getItems().clear();
			documentationNavigation.getSelectionModel().clearSelection();
			launchButton.setDisable(true);
			if (experiment != null) {
				loadDocumentation(experiment.documentationResource());
			}
		});
		documentationNavigation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, section) -> {
			documentationButton.setDisable(section == null);
			if (section != null) {
				pendingAnchor = section.anchor();
				if (documentationEngine.getLoadWorker().getState() == State.SUCCEEDED) {
					typesetMathAndScrollTo(pendingAnchor);
				}
			}
		});
		documentationButton.setOnAction(event -> {
			DocumentationSection section = documentationNavigation.getSelectionModel().getSelectedItem();
			if (section != null && documentationEngine.getLoadWorker().getState() == State.SUCCEEDED) {
				pendingAnchor = section.anchor();
				typesetMathAndScrollTo(pendingAnchor);
			}
		});
		beginningButton.setOnAction(event -> scrollToBeginning());
		launcherList.getSelectionModel().selectedItemProperty().addListener(selectionListener(launchButton));
		launchButton.setOnAction(event -> launch(launcherList.getSelectionModel().getSelectedItem()));

		VBox launcherPane = new VBox(10,
				new Label("Experiment"), experimentSelection,
				new Label("Documentation"), documentationNavigation, documentationButton, beginningButton,
				new Separator(), new Label("Experiment launchers"), launcherList, launchButton);
		launcherPane.setPadding(new Insets(12));
		VBox.setVgrow(launcherList, Priority.ALWAYS);
		HBox footer = new HBox(status);
		footer.setPadding(new Insets(8, 12, 12, 12));

		BorderPane root = new BorderPane(documentationView);
		root.setLeft(launcherPane);
		root.setBottom(footer);
		stage.setTitle("MARLKIT — Experiment launcher");
		stage.setScene(new Scene(root, 1200, 760));
		stage.show();

		// Show the first experiment's README while keeping launcher selection empty.
		experimentSelection.getSelectionModel().selectFirst();
	}

	private ChangeListener<LauncherDescriptor> selectionListener(Button launchButton) {
		return (observable, oldValue, selectedLauncher) -> {
			launchButton.setDisable(selectedLauncher == null);
			if (selectedLauncher != null) {
				status.setText("Ready to launch " + selectedLauncher.title() + " in a separate JVM.");
				pendingAnchor = selectedLauncher.documentationAnchor();
				if (documentationEngine.getLoadWorker().getState() == State.SUCCEEDED) {
					typesetMathAndScrollTo(pendingAnchor);
				}
			}
		};
	}

	private void loadDocumentation(String resource) {
		URL readmeUrl = MarkdownDocumentation.resourceUrl(ExperimentLauncherMenu.class, resource);
		String renderedMarkdown = MarkdownDocumentation.renderMarkdown(
				MarkdownDocumentation.readResource(ExperimentLauncherMenu.class, resource));
		String resourceBase = readmeUrl.toExternalForm()
				.substring(0, readmeUrl.toExternalForm().lastIndexOf('/') + 1);
		URL sharedMathJax = ExperimentLauncherMenu.class
				.getResource("/marlkit/collectingresource/mathjax/tex-svg.js");
		String mathJaxResource = sharedMathJax == null
				? resourceBase + "mathjax/tex-svg.js"
				: sharedMathJax.toExternalForm();
		String html = """
				<!doctype html>
				<html><head><base href=\"%s\"><style>
				body { font-family: sans-serif; line-height: 1.5; margin: 24px; color: #202124; }
				img { max-width: 100%%; height: auto; }
				code { background: #f1f3f4; padding: 2px 4px; border-radius: 3px; }
				h1, h2, h3 { scroll-margin-top: 12px; }
				</style><script>
				window.MathJax = { tex: { inlineMath: [['\\\\(', '\\\\)'], ['$', '$']], displayMath: [['$$', '$$']] }, svg: { fontCache: 'global' } };
				</script><script src=\"%s\"></script><script>
				function slug(value) { return value.toLowerCase().trim().replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, ''); }
				window.addEventListener('DOMContentLoaded', () => document.querySelectorAll('h1,h2,h3,h4,h5,h6').forEach(h => h.id = slug(h.textContent)));
				</script></head><body>%s</body></html>
				""".formatted(resourceBase, mathJaxResource, renderedMarkdown);
		documentationEngine.loadContent(html, "text/html");
		// Populate navigation after parsing the selected experiment's README.
		documentationNavigation.getItems().setAll(MarkdownDocumentation.sections(
				MarkdownDocumentation.readResource(ExperimentLauncherMenu.class, resource)));
	}

	private void typesetMathAndScrollTo(String anchor) {
		documentationEngine.executeScript("if (window.MathJax && window.MathJax.typesetPromise) { MathJax.typesetPromise().then(() => document.getElementById('"
				+ anchor + "')?.scrollIntoView({behavior: 'smooth', block: 'start'})); } else { document.getElementById('"
				+ anchor + "')?.scrollIntoView({behavior: 'smooth', block: 'start'}); }");
		pendingAnchor = null;
	}

	private void scrollToBeginning() {
		pendingAnchor = null;
		documentationEngine.executeScript("window.scrollTo({top: 0, behavior: 'smooth'});");
	}

	private void launch(LauncherDescriptor launcher) {
		if (launcher == null) {
			return;
		}
		try {
			Process process = LauncherProcessStarter.start(launcher.launcherClass());
			status.setText("Started " + launcher.title() + " (PID " + process.pid() + "). The menu remains available.");
		} catch (Exception exception) {
			status.setText("Unable to start " + launcher.title() + ": " + exception.getMessage());
		}
	}

	public static void main(String[] args) {
		Application.launch(ExperimentLauncherMenu.class, args);
	}
}
