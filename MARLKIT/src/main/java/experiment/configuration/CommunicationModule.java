package experiment.configuration;

import java.util.Objects;

import communication.CommunicationModel;

/**
 * Module responsible for creating communication models for agents.
 * <p>
 * A {@code CommunicationModule} stores the communication model class selected for an
 * experiment configuration and creates fresh instances of that model when agents are
 * built.
 * </p>
 * <p>
 * This module is intentionally simple: it does not define communication behavior itself.
 * The behavior is defined by the {@link CommunicationModel} implementation it instantiates.
 * </p>
 */
public class CommunicationModule {

    private final Class<? extends CommunicationModel> communicationModelClass;

    /**
     * Creates a communication module using the given communication model class.
     *
     * @param communicationModelClass the communication model class to instantiate
     */
    public CommunicationModule(Class<? extends CommunicationModel> communicationModelClass) {
        this.communicationModelClass = Objects.requireNonNull(communicationModelClass, "communicationModelClass");
    }

    /**
     * Creates a fresh communication model instance.
     *
     * @return a new communication model
     */
    public CommunicationModel createCommunicationModel() {
        try {
            return communicationModelClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot instantiate communication model: " + communicationModelClass.getName(), e);
        }
    }

    /**
     * Returns the communication model class used by this module.
     *
     * @return the communication model class
     */
    public Class<? extends CommunicationModel> getCommunicationModelClass() {
        return communicationModelClass;
    }
}