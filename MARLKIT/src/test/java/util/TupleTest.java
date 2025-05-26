package util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class TupleTest {

    @Test
    public void testConstructorAndGetValue() {
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
    public void testGetSize() {
        // Given
        List<Double> values = Arrays.asList(1.0, 2.0, 3.0);
        Tuple tuple = new Tuple(values);
        
        // When & Then
        assertThat(tuple.getSize()).isEqualTo(3);
    }
    
    @Test
    public void testSetValue() {
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
    public void testAdd() {
        // Given
        List<Double> values1 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> values2 = Arrays.asList(4.0, 5.0, 6.0);
        Tuple tuple1 = new Tuple(values1);
        Tuple tuple2 = new Tuple(values2);
        
        // When
        Tuple result = tuple1.add(tuple2);
        
        // Then - note that the current implementation has a bug in the add method
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(3);
        
         assertThat(result.getValue(0)).isEqualTo(5.0);
         assertThat(result.getValue(1)).isEqualTo(7.0);
         assertThat(result.getValue(2)).isEqualTo(9.0);
    }
    
    @Test
    public void testAddWithDifferentSizes() {
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
    public void testMultiply() {
        // Given
        List<Double> values = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        Tuple tuple = new Tuple(values);
        
        // When
        Tuple result = tuple.multiply(2.0);
        
        // Then - note that the current implementation has a bug in the multiply method
        assertThat(result).isNotNull();
        assertThat(result.getSize()).isEqualTo(3);
        
         assertThat(result.getValue(0)).isEqualTo(2.0);
         assertThat(result.getValue(1)).isEqualTo(4.0);
         assertThat(result.getValue(2)).isEqualTo(6.0);
    }
    
    @Test
    public void testClone() {
        // Given
        List<Double> values = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));
        Tuple originalTuple = new Tuple(values);
        
        // When
        Tuple clonedTuple = originalTuple.clone();
        
        // Then
        assertThat(clonedTuple).isNotNull();
        assertThat(clonedTuple.getSize()).isEqualTo(3);
        
        assertThat(clonedTuple.getValue(0)).isEqualTo(1.0);
        assertThat(clonedTuple.getValue(1)).isEqualTo(2.0);
        assertThat(clonedTuple.getValue(2)).isEqualTo(3.0);
        assertThat(clonedTuple).isNotSameAs(originalTuple);
    }
    
    @Test
    public void testToString() {
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
    public void testEquals() {
        // Given
        List<Double> values1 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> values2 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> values3 = Arrays.asList(4.0, 5.0, 6.0);
        Tuple tuple1 = new Tuple(values1);
        Tuple tuple2 = new Tuple(values2);
        Tuple tuple3 = new Tuple(values3);
        
        // When & Then
        assertThat(tuple1.equals(tuple2)).isTrue();
        assertThat(tuple1.equals(tuple3)).isFalse();
        assertThat(tuple1.equals(tuple1)).isTrue();
        assertThat(tuple1.equals("not a tuple")).isFalse();
    }
    
    @Test
    public void testHashCode() {
        // Given
        List<Double> values1 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> values2 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> values3 = Arrays.asList(4.0, 5.0, 6.0);
        Tuple tuple1 = new Tuple(values1);
        Tuple tuple2 = new Tuple(values2);
        Tuple tuple3 = new Tuple(values3);
        
        // When & Then
        assertThat(tuple1.hashCode()).isEqualTo(tuple2.hashCode());
        assertThat(tuple1.hashCode()).isNotEqualTo(tuple3.hashCode());
    }
}
