package marlkit.preyhunter.environment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.policies.PolicyInput;
import util.Tuple;

/**
 * Converts PreyHunter variable-size observations into fixed-size vectors.
 *
 * Format:
 * - maxHunters slots: [dx, dy, value]
 * - maxPreys slots:   [dx, dy, value]
 *
 * Empty slots are filled with [0, 0, 0].
 *
 * Example:
 * [dxHunter1, dyHunter1, OBS_HUNTER,
 *  0,         0,         0,
 *  dxPrey,    dyPrey,    OBS_PREY]
 */
public class WrapperPreyHunterObservationVector implements WrapperPolicyInputVector {

    private static final int VALUES_PER_ENTITY = 3;

    private final int maxHunters;
    private final int maxPreys;

    public WrapperPreyHunterObservationVector(int maxHunters, int maxPreys) {
        if (maxHunters < 0 || maxPreys < 0) {
            throw new IllegalArgumentException("maxHunters and maxPreys must be >= 0.");
        }

        this.maxHunters = maxHunters;
        this.maxPreys = maxPreys;
    }

    @Override
    public double[] transform(PolicyInput input) {
        if (!(input instanceof ObservationPositionsValues observation)) {
            throw new IllegalArgumentException("Expected ObservationPositionsValues.");
        }

        double[] vector = new double[getVectorSize()];

        List<ObservationPositionValue> hunters = filterByValue(
                observation,
                StatePreyHunter2D.OBS_HUNTER
        );

        List<ObservationPositionValue> preys = filterByValue(
                observation,
                StatePreyHunter2D.OBS_PREY
        );

        sortByDistance(hunters);
        sortByDistance(preys);

        fillSlots(vector, 0, hunters, maxHunters);

        int preyOffset = maxHunters * VALUES_PER_ENTITY;
        fillSlots(vector, preyOffset, preys, maxPreys);

        return vector;
    }

    @Override
    public ObservationPositionsValues transform(double[] vector) {
        if (vector.length != getVectorSize()) {
            throw new IllegalArgumentException(
                    "Expected vector size " + getVectorSize() + " but got " + vector.length
            );
        }

        ObservationPositionsValues observation = new ObservationPositionsValues();

        readSlots(observation, vector, 0, maxHunters);

        int preyOffset = maxHunters * VALUES_PER_ENTITY;
        readSlots(observation, vector, preyOffset, maxPreys);

        return observation;
    }

    public int getVectorSize() {
        return (maxHunters + maxPreys) * VALUES_PER_ENTITY;
    }

    private List<ObservationPositionValue> filterByValue(
            ObservationPositionsValues observation,
            double value
    ) {
        List<ObservationPositionValue> filtered = new ArrayList<>();

        for (ObservationPositionValue obs : observation.getListObs()) {
            if (obs.getValue() == value) {
                filtered.add(obs);
            }
        }

        return filtered;
    }

    private void sortByDistance(List<ObservationPositionValue> observations) {
        observations.sort(Comparator.comparingDouble(this::distance));
    }

    private double distance(ObservationPositionValue observation) {
        Tuple position = observation.getPosition();

        double dx = position.getValue(0);
        double dy = position.getValue(1);

        return Math.sqrt(dx * dx + dy * dy);
    }

    private void fillSlots(
            double[] vector,
            int offset,
            List<ObservationPositionValue> observations,
            int maxSlots
    ) {
        int numberToWrite = Math.min(observations.size(), maxSlots);

        for (int i = 0; i < numberToWrite; i++) {
            ObservationPositionValue obs = observations.get(i);
            Tuple position = obs.getPosition();

            int index = offset + i * VALUES_PER_ENTITY;

            vector[index] = position.getValue(0);
            vector[index + 1] = position.getValue(1);
            vector[index + 2] = obs.getValue();
        }
    }

    private void readSlots(
            ObservationPositionsValues observation,
            double[] vector,
            int offset,
            int maxSlots
    ) {
        for (int i = 0; i < maxSlots; i++) {
            int index = offset + i * VALUES_PER_ENTITY;

            double dx = vector[index];
            double dy = vector[index + 1];
            double value = vector[index + 2];

            if (value == 0.0) {
                continue;
            }

            Tuple position = new Tuple(List.of(dx, dy));

            observation.addObservationPosition(
                    new ObservationPositionValue(position, value)
            );
        }
    }
}
