package experiment.configuration;

import java.util.Objects;

import evaluation.SystemEvaluator;

/**
 * Module responsible for creating system evaluators for experiment runs.
 * <p>
 * A {@code SystemEvaluatorModule} stores the {@link SystemEvaluator} implementation
 * selected for an experiment configuration and creates a fresh evaluator instance
 * when the experiment is built.
 * </p>
 * <p>
 * The evaluator itself computes system-level measures such as total reward,
 * success rate, fairness, capture time, or any other global metric useful for
 * comparing configurations.
 * </p>
 * <p>
 * This simple module assumes that the evaluator class exposes a no-argument
 * constructor. If an evaluator requires parameters, a specialized module or
 * factory-based variant can be introduced later.
 * </p>
 */
public class SystemEvaluatorModule {

    private final Class<? extends SystemEvaluator> systemEvaluatorClass;

    /**
     * Creates a system evaluator module using the given evaluator class.
     *
     * @param systemEvaluatorClass the system evaluator class to instantiate
     */
    public SystemEvaluatorModule(Class<? extends SystemEvaluator> systemEvaluatorClass) {
        this.systemEvaluatorClass = Objects.requireNonNull(systemEvaluatorClass, "systemEvaluatorClass");
    }

    /**
     * Creates a fresh system evaluator instance.
     *
     * @return a new system evaluator
     */
    public SystemEvaluator createSystemEvaluator() {
        try {
            return systemEvaluatorClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot instantiate system evaluator: " + systemEvaluatorClass.getName(), e);
        }
    }

    /**
     * Returns the system evaluator class used by this module.
     *
     * @return the system evaluator class
     */
    public Class<? extends SystemEvaluator> getSystemEvaluatorClass() {
        return systemEvaluatorClass;
    }
}