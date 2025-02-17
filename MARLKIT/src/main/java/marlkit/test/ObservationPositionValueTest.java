
package marlkit.test;

import java.util.Arrays;

import environment.observation.ObservationPositionValue;
import util.Tuple;

public class ObservationPositionValueTest {

    public static void main(String[] args) {
        testGivenSameValuesWhenEqualsThenReturnTrue();
        testGivenDifferentValuesWhenEqualsThenReturnFalse();
        testGivenNullObjectWhenEqualsThenReturnFalse();
        testGivenDifferentClassObjectWhenEqualsThenReturnFalse();
        testGivenSameObjectWhenEqualsThenReturnTrue();
    }

    public static void testGivenSameValuesWhenEqualsThenReturnTrue() {
        // Given
        Tuple position1 = new Tuple(Arrays.asList(1.0, 2.0));
        Tuple position2 = new Tuple(Arrays.asList(1.0, 2.0));
        ObservationPositionValue obs1 = new ObservationPositionValue(position1, 10.0);
        ObservationPositionValue obs2 = new ObservationPositionValue(position2, 10.0);

        // When
        boolean result = obs1.equals(obs2);

        // Then
        if (result) {
            System.out.println("testGivenSameValuesWhenEqualsThenReturnTrue passed");
        } else {
            System.out.println("testGivenSameValuesWhenEqualsThenReturnTrue failed");
        }
    }

    public static void testGivenDifferentValuesWhenEqualsThenReturnFalse() {
        // Given
        Tuple position1 = new Tuple(Arrays.asList(1.0, 2.0));
        Tuple position2 = new Tuple(Arrays.asList(3.0, 4.0));
        ObservationPositionValue obs1 = new ObservationPositionValue(position1, 10.0);
        ObservationPositionValue obs2 = new ObservationPositionValue(position2, 20.0);

        // When
        boolean result = obs1.equals(obs2);

        // Then
        if (!result) {
            System.out.println("testGivenDifferentValuesWhenEqualsThenReturnFalse passed");
        } else {
            System.out.println("testGivenDifferentValuesWhenEqualsThenReturnFalse failed");
        }
    }

    public static void testGivenNullObjectWhenEqualsThenReturnFalse() {
        // Given
        Tuple position = new Tuple(Arrays.asList(1.0, 2.0));
        ObservationPositionValue obs = new ObservationPositionValue(position, 10.0);

        // When
        boolean result = obs.equals(null);

        // Then
        if (!result) {
            System.out.println("testGivenNullObjectWhenEqualsThenReturnFalse passed");
        } else {
            System.out.println("testGivenNullObjectWhenEqualsThenReturnFalse failed");
        }
    }

    public static void testGivenDifferentClassObjectWhenEqualsThenReturnFalse() {
        // Given
        Tuple position = new Tuple(Arrays.asList(1.0, 2.0));
        ObservationPositionValue obs = new ObservationPositionValue(position, 10.0);
        String differentClassObject = "Not an ObservationPositionValue";

        // When
        boolean result = obs.equals(differentClassObject);

        // Then
        if (!result) {
            System.out.println("testGivenDifferentClassObjectWhenEqualsThenReturnFalse passed");
        } else {
            System.out.println("testGivenDifferentClassObjectWhenEqualsThenReturnFalse failed");
        }
    }

    public static void testGivenSameObjectWhenEqualsThenReturnTrue() {
        // Given
        Tuple position = new Tuple(Arrays.asList(1.0, 2.0));
        ObservationPositionValue obs = new ObservationPositionValue(position, 10.0);

        // When
        boolean result = obs.equals(obs);

        // Then
        if (result) {
            System.out.println("testGivenSameObjectWhenEqualsThenReturnTrue passed");
        } else {
            System.out.println("testGivenSameObjectWhenEqualsThenReturnTrue failed");
        }
    }
}
