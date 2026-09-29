package marlkit.launcher;

/** A README heading that can be selected for documentation navigation. */
public record DocumentationSection(String title, String anchor) {
	@Override
	public String toString() {
		return title;
	}
}
