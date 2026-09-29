package simulation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares user-facing metadata for a concrete {@link MLKLauncher}.
 * <p>
 * Launcher selection applications discover this annotation at runtime. The
 * documentation anchor must identify a heading in the experiment README.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LauncherMetadata {
	/**
	 * Human-readable launcher name displayed to users.
	 *
	 * @return the launcher title
	 */
	String title();

	/**
	 * Identifier of the documentation heading describing this launcher.
	 *
	 * @return the documentation anchor
	 */
	String documentationAnchor();
}
