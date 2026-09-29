package marlkit.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/** Common Markdown, resource, and README-navigation support for launcher UIs. */
public final class MarkdownDocumentation {
	private static final Pattern HEADING = Pattern.compile("^(#{2,3})\\s+(.+)$", Pattern.MULTILINE);

	private MarkdownDocumentation() {
	}

	public static String readResource(Class<?> resourceOwner, String resourceName) {
		try (InputStream stream = resourceOwner.getResourceAsStream(resourceName)) {
			if (stream == null) {
				throw new IllegalStateException("Missing resource " + resourceName);
			}
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read resource " + resourceName, exception);
		}
	}

	public static URL resourceUrl(Class<?> resourceOwner, String resourceName) {
		URL url = resourceOwner.getResource(resourceName);
		if (url == null) {
			throw new IllegalStateException("Missing resource " + resourceName);
		}
		return url;
	}

	public static String renderMarkdown(String markdown) {
		var extensions = List.of(TablesExtension.create());
		Parser parser = Parser.builder().extensions(extensions).build();
		return HtmlRenderer.builder()
				.extensions(extensions)
				.escapeHtml(false)
				.build()
				.render(parser.parse(markdown));
	}

	public static List<DocumentationSection> sections(String markdown) {
		Matcher matcher = HEADING.matcher(markdown);
		List<DocumentationSection> sections = new ArrayList<>();
		while (matcher.find()) {
			String title = matcher.group(2).trim();
			sections.add(new DocumentationSection(title, anchor(title)));
		}
		return List.copyOf(sections);
	}

	public static String anchor(String title) {
		return title.toLowerCase()
				.trim()
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-+|-+$", "");
	}
}
