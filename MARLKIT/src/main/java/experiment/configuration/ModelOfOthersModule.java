package experiment.configuration;

import agent.modelofotheragent.ModelsManager;

/**
 * Module responsible for creating models-manager instances for agents that model other agents.
 * <p>
 * If the provided {@code modelsManagerClass} is {@code null}, this module represents the
 * absence of model-of-other-agents mechanism and returns {@code null}.
 * </p>
 */
public class ModelOfOthersModule {

    private final Class<? extends ModelsManager> modelsManagerClass;

    /**
     * Creates a model-of-others module.
     *
     * @param modelsManagerClass the models-manager class to instantiate, or {@code null}
     *                           if no model-of-others mechanism should be used
     */
    public ModelOfOthersModule(Class<? extends ModelsManager> modelsManagerClass) {
        this.modelsManagerClass = modelsManagerClass;
    }

    /**
     * Creates a fresh models-manager instance.
     *
     * @return a new models-manager instance, or {@code null} if no model-of-others is configured
     */
    public ModelsManager createModelsManager() {
        if (modelsManagerClass == null) {
            return null;
        }

        try {
            return modelsManagerClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot instantiate models manager: " + modelsManagerClass.getName(), e);
        }
    }

    /**
     * Returns the models-manager class used by this module.
     *
     * @return the models-manager class, or {@code null} if disabled
     */
    public Class<? extends ModelsManager> getModelsManagerClass() {
        return modelsManagerClass;
    }

    /**
     * Checks whether this module disables model-of-other-agents.
     *
     * @return true if no models manager is configured
     */
    public boolean isDisabled() {
        return modelsManagerClass == null;
    }
}