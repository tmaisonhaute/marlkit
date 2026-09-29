package marlkit.collectingresource.launchers;

import java.util.List;

import marlkit.launcher.ExperimentLauncherMenu;
import marlkit.launcher.MarkdownDocumentation;

/**
 * Compatibility entry point for the former collecting-resource menu.
 * The generic {@link ExperimentLauncherMenu} now provides the implementation.
 */
@Deprecated(forRemoval = false)
public final class CollectingResourceLauncherMenu {
	private CollectingResourceLauncherMenu() {
	}

	public static String renderMarkdown(String markdown) {
		return MarkdownDocumentation.renderMarkdown(markdown);
	}

	public static List<DocumentationSection> documentationSections(String markdown) {
		return MarkdownDocumentation.sections(markdown).stream()
				.map(section -> new DocumentationSection(section.title(), section.anchor()))
				.toList();
	}

	public record DocumentationSection(String title, String anchor) {
		@Override
		public String toString() {
			return title;
		}
	}

	public static void main(String[] args) {
		ExperimentLauncherMenu.main(args);
	}
}