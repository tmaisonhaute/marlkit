package simulation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the documentation metadata for a package of experiment launchers.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface ExperimentMetadata {
	/**
	 * Human-readable experiment title displayed by launcher browsers.
	 *
	 * @return the experiment title
	 */
	String title();

	/**
	 * Classpath resource containing the experiment README.
	 *
	 * @return an absolute resource path such as {@code /marlkit/foo/README.md}
	 */
	String documentationResource();

	/**
	 * Ordering among experiments in a launcher browser.
	 *
	 * @return the display order
	 */
	int order() default 0;
}
