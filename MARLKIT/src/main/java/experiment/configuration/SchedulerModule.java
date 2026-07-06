package experiment.configuration;

import java.util.Objects;

import simulation.MLKScheduler;

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

    /**
     * Creates a scheduler module.
     *
     * @param schedulerClass the scheduler class to instantiate
     */
    public SchedulerModule(Class<? extends MLKScheduler> schedulerClass) {
        this.schedulerClass = Objects.requireNonNull(schedulerClass, "schedulerClass");
    }

    /**
     * Creates a fresh scheduler instance.
     *
     * @return a new scheduler
     */
    public MLKScheduler createScheduler() {
        try {
            return schedulerClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot instantiate scheduler: " + schedulerClass.getName(),
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