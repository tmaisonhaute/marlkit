package experiment.configuration;

import java.util.Objects;

import simulation.MLKScheduler;
import trainingexecutionstrategy.DecentralizedTrainingExecutionStrategy;
import trainingexecutionstrategy.TrainingExecutionStrategy;

/**
 * Module responsible for creating the scheduler of an experiment.
 * <p>
 * This module stores the concrete scheduler class selected for a configuration
 * and creates a fresh scheduler instance when the experiment is built.
 * </p>
 * <p>
 * The scheduler class must expose a no-argument constructor. In the current
 * MARLKIT style, scheduler criteria are usually configured inside the scheduler
 * constructor or subclass.
 * </p>
 */
public class SchedulerModule {

    private final Class<? extends MLKScheduler> schedulerClass;
    private final Class<? extends TrainingExecutionStrategy> trainingExecutionStrategyClass;

    /**
     * Creates a scheduler module.
     * The training execution strategy will default to {@link DecentralizedTrainingExecutionStrategy}.
     *
     * @param schedulerClass the scheduler class to instantiate
     */
    public SchedulerModule(Class<? extends MLKScheduler> schedulerClass) {
        this.schedulerClass = Objects.requireNonNull(schedulerClass, "schedulerClass");
        this.trainingExecutionStrategyClass = DecentralizedTrainingExecutionStrategy.class;
    }
    
    /**
     * Creates a scheduler module.
     * The training execution strategy will be set to the provided class.
     *
     * @param schedulerClass the scheduler class to instantiate
     * @param trainingExecutionStrategyClass the training execution strategy class to use
     */
    public SchedulerModule(Class<? extends MLKScheduler> schedulerClass, Class<? extends TrainingExecutionStrategy> trainingExecutionStrategyClass) {
        this.schedulerClass = Objects.requireNonNull(schedulerClass, "schedulerClass");
        this.trainingExecutionStrategyClass = Objects.requireNonNull(trainingExecutionStrategyClass, "trainingExecutionStrategyClass");
    }

    /**
     * Creates a fresh scheduler instance.
     *
     * @return a new scheduler
     */
    public MLKScheduler createScheduler() {
        try {
        	MLKScheduler scheduler =  schedulerClass.getDeclaredConstructor().newInstance();
        	TrainingExecutionStrategy strategy = trainingExecutionStrategyClass.getDeclaredConstructor().newInstance();
        	
        	scheduler.setTrainingExecutionStrategy(strategy);
            return scheduler;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot instantiate scheduler [" + schedulerClass.getName() 
                    + "] or training execution strategy [" + trainingExecutionStrategyClass.getName() + "]",
                    e
            );
        }
    }

    /**
     * Returns the scheduler class used by this module.
     *
     * @return the scheduler class
     */
    public Class<? extends MLKScheduler> getSchedulerClass() {
        return schedulerClass;
    }
}