package experiment.configuration;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import environment.MLKEnvironment;
import reward.RewardModel;

public class ConfigurableEnvironmentModule extends EnvironmentModule {

    private final Object[] constructorArguments;

    public ConfigurableEnvironmentModule(Class<? extends MLKEnvironment> environmentClass, Object... constructorArguments) {
        super(environmentClass);
        this.constructorArguments = constructorArguments.clone();
    }

    @Override
    public MLKEnvironment createEnvironment(RewardModel rewardModel) {
        Object[] arguments = Arrays.copyOf(constructorArguments, constructorArguments.length + 1);

        arguments[arguments.length - 1] = rewardModel;

        for (Constructor<?> constructor : getEnvironmentClass().getConstructors()) {
            if (isCompatible(constructor.getParameterTypes(), arguments)) {
                try {
                    return (MLKEnvironment) constructor.newInstance(arguments);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("Cannot instantiate environment: " + getEnvironmentClass().getName(), e);
                }
            }
        }

        throw new IllegalStateException("No compatible constructor found for environment: " + getEnvironmentClass().getName());
    }

    private boolean isCompatible(Class<?>[] parameterTypes, Object[] arguments) {
        if (parameterTypes.length != arguments.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (!wrap(parameterTypes[i]).isInstance(arguments[i])) {
                return false;
            }
        }

        return true;
    }

    private Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }

        if (type == int.class) {
            return Integer.class;
        }

        if (type == double.class) {
            return Double.class;
        }

        if (type == boolean.class) {
            return Boolean.class;
        }

        if (type == long.class) {
            return Long.class;
        }

        if (type == float.class) {
            return Float.class;
        }

        if (type == short.class) {
            return Short.class;
        }

        if (type == byte.class) {
            return Byte.class;
        }

        if (type == char.class) {
            return Character.class;
        }

        return type;
    }
}