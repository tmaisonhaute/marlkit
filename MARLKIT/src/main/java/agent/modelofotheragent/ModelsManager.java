package agent.modelofotheragent;

/**
 * Root contract for managers of models of other agents.
 *
 * <p>This interface serves as a common type for components that
 * store, expose, and coordinate prediction models used by learning agents that reason about
 * others. Concrete managers typically provide accessors to one or more {@link ModelPredictAction}
 * or {@link GroupModelPredictAction} instances and are used by agents that implement
 * {@link MLKAgentPredictingOthersAction}.</p>
 *
 * <p>See {@link PredictionModelsManager} for the default implementation backed by a group
 * prediction model.</p>
 */
public interface ModelsManager {

}
