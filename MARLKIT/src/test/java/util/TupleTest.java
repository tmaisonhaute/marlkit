package util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class TupleTest {

    @Test
    public void givenTuple_whenGetValue_thenReturnsCorrectElement() {
        // Given
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0);

        // When
        Tuple tuple = new Tuple(values);

        // Then
        assertThat(tuple.getValue(0)).isEqualTo(1.0);
        assertThat(tuple.getValue(1)).isEqualTo(2.0);
        assertThat(tuple.getValue(2)).isEqualTo(3.0);
    }

    @Test
    public void givenTuple_whenGetSize_thenReturnsCorrectSize() {
        // Given
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0);
        Tuple tuple = new Tuple(values);

        // When
        int size = tuple.getSize();

        // Then
        assertThat(size).isEqualTo(3);
    }

    @Test
    public void givenTuple_whenSetValue_thenValueIsUpdated() {
        // Given
        List<Double> values = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        Tuple tuple = new Tuple(values);

        // When
        tuple.setValue(1, 5.0);

        // Then
        assertThat(tuple.getValue(0)).isEqualTo(1.0);
        assertThat(tuple.getValue(1)).isEqualTo(5.0);
        assertThat(tuple.getValue(2)).isEqualTo(3.0);
    }

    @Test
    public void givenTwoTuplesOfSameSize_whenAdd_thenReturnsElementWiseSumTuple() {
        // Given
        List<Double> values1 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> values2 = Arrays.asList(4.0, 5.0, 6.0);
        Tuple tuple1 = new Tuple(values1);
        Tuple tuple2 = new Tuple(values2);

        // When
        Tuple result = Tuple.add(tuple1, tuple2);

        // Then
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getValue(0)).isEqualTo(5.0);
        assertThat(result.getValue(1)).isEqualTo(7.0);
        assertThat(result.getValue(2)).isEqualTo(9.0);
    }

    @Test
    public void givenTuplesOfDifferentSizes_whenAdd_thenThrowsException() {
        // Given
        List<Double> values1 = Arrays.asList(1.0, 2.0);
        List<Double> values2 = Arrays.asList(4.0, 5.0, 6.0);
        Tuple tuple1 = new Tuple(values1);
        Tuple tuple2 = new Tuple(values2);

        // When & Then
        assertThatThrownBy(() -> tuple1.add(tuple2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tuple of differents size can't be added");
    }

    @Test
    public void givenTuple_whenMultiply_thenReturnsScaledTuple() {
        // Given
        List<Double> values = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        Tuple tuple = new Tuple(values);

        // When
        Tuple result = Tuple.multiply(tuple, 2.0);

        // Then
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getValue(0)).isEqualTo(2.0);
        assertThat(result.getValue(1)).isEqualTo(4.0);
        assertThat(result.getValue(2)).isEqualTo(6.0);
    }

    @Test
    public void givenTuple_whenMultiplyByZero_thenReturnsZeroTuple() {
        // Given
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0);
        Tuple tuple = new Tuple(values);

        // When
        Tuple result = Tuple.multiply(tuple, 0.0);

        // Then
        assertThat(result.getValue(0)).isEqualTo(0.0);
        assertThat(result.getValue(1)).isEqualTo(0.0);
        assertThat(result.getValue(2)).isEqualTo(0.0);
    }

    @Test
    public void givenTuple_whenClone_thenReturnsEqualButDifferentInstance() {
        // Given
        List<Double> values = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        Tuple originalTuple = new Tuple(values);

        // When
        Tuple clonedTuple = originalTuple.copy();

        // Then
        assertThat(clonedTuple).isEqualTo(originalTuple);
        assertThat(clonedTuple).isNotSameAs(originalTuple);
        assertThat(clonedTuple.getValue(0)).isEqualTo(1.0);
        assertThat(clonedTuple.getValue(1)).isEqualTo(2.0);
        assertThat(clonedTuple.getValue(2)).isEqualTo(3.0);
    }

    @Test
    public void givenClonedTuple_whenModified_thenOriginalIsUnchanged() {
        // Given
        List<Double> values = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        Tuple originalTuple = new Tuple(values);
        Tuple clonedTuple = originalTuple.copy();

        // When
        clonedTuple.setValue(0, 99.0);

        // Then
        assertThat(originalTuple.getValue(0)).isEqualTo(1.0);
    }

    @Test
    public void givenTuple_whenToString_thenReturnsReadableRepresentation() {
        // Given
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0);
        Tuple tuple = new Tuple(values);

        // When
        String result = tuple.toString();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("1.0", "2.0", "3.0");
    }

    @Test
    public void givenTwoTuplesWithSameValues_whenEquals_thenReturnsTrue() {
        // Given
        Tuple tuple1 = new Tuple(Arrays.asList(1.0, 2.0, 3.0));
        Tuple tuple2 = new Tuple(Arrays.asList(1.0, 2.0, 3.0));

        // When
        boolean result = tuple1.equals(tuple2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void givenTwoTuplesWithDifferentValues_whenEquals_thenReturnsFalse() {
        // Given
        Tuple tuple1 = new Tuple(Arrays.asList(1.0, 2.0, 3.0));
        Tuple tuple2 = new Tuple(Arrays.asList(4.0, 5.0, 6.0));

        // When
        boolean result = tuple1.equals(tuple2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void givenTupleComparedToItself_whenEquals_thenReturnsTrue() {
        // Given
        Tuple tuple = new Tuple(Arrays.asList(1.0, 2.0, 3.0));

        // When
        boolean result = tuple.equals(tuple);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void givenTupleComparedToNonTuple_whenEquals_thenReturnsFalse() {
        // Given
        Tuple tuple = new Tuple(Arrays.asList(1.0, 2.0, 3.0));

        // When
        boolean result = tuple.equals("not a tuple");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void givenTwoTuplesWithSameValues_whenHashCode_thenReturnsSameHashCode() {
        // Given
        Tuple tuple1 = new Tuple(Arrays.asList(1.0, 2.0, 3.0));
        Tuple tuple2 = new Tuple(Arrays.asList(1.0, 2.0, 3.0));

        // When & Then
        assertThat(tuple1.hashCode()).isEqualTo(tuple2.hashCode());
    }

    @Test
    public void givenTwoTuplesWithDifferentValues_whenHashCode_thenReturnsDifferentHashCodes() {
        // Given
        Tuple tuple1 = new Tuple(Arrays.asList(1.0, 2.0, 3.0));
        Tuple tuple2 = new Tuple(Arrays.asList(4.0, 5.0, 6.0));

        // When & Then
        assertThat(tuple1.hashCode()).isNotEqualTo(tuple2.hashCode());
    }
}
