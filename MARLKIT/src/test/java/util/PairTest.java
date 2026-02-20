package util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class PairTest {

    @Test
    public void givenPair_whenGetFirst_thenReturnsFirstElement() {
        // Given
        Pair<String, Integer> pair = new Pair<>("hello", 42);

        // When
        String result = pair.getFirst();

        // Then
        assertThat(result).isEqualTo("hello");
    }

    @Test
    public void givenPair_whenGetSecond_thenReturnsSecondElement() {
        // Given
        Pair<String, Integer> pair = new Pair<>("hello", 42);

        // When
        Integer result = pair.getSecond();

        // Then
        assertThat(result).isEqualTo(42);
    }

    @Test
    public void givenPair_whenSetFirst_thenFirstElementIsUpdated() {
        // Given
        Pair<String, Integer> pair = new Pair<>("hello", 42);

        // When
        pair.setFirst("world");

        // Then
        assertThat(pair.getFirst()).isEqualTo("world");
        assertThat(pair.getSecond()).isEqualTo(42);
    }

    @Test
    public void givenPair_whenSetSecond_thenSecondElementIsUpdated() {
        // Given
        Pair<String, Integer> pair = new Pair<>("hello", 42);

        // When
        pair.setSecond(100);

        // Then
        assertThat(pair.getFirst()).isEqualTo("hello");
        assertThat(pair.getSecond()).isEqualTo(100);
    }

    @Test
    public void givenPair_whenClone_thenReturnsEqualButDifferentInstance() {
        // Given
        Pair<String, Integer> pair = new Pair<>("hello", 42);

        // When
        Pair<String, Integer> cloned = pair.clone();

        // Then
        assertThat(cloned).isEqualTo(pair);
        assertThat(cloned).isNotSameAs(pair);
        assertThat(cloned.getFirst()).isEqualTo("hello");
        assertThat(cloned.getSecond()).isEqualTo(42);
    }

    @Test
    public void givenPair_whenToString_thenReturnsReadableRepresentation() {
        // Given
        Pair<String, Integer> pair = new Pair<>("hello", 42);

        // When
        String result = pair.toString();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("hello", "42");
    }

    @Test
    public void givenTwoPairsWithSameValues_whenEquals_thenReturnsTrue() {
        // Given
        Pair<String, Integer> pair1 = new Pair<>("hello", 42);
        Pair<String, Integer> pair2 = new Pair<>("hello", 42);

        // When
        boolean result = pair1.equals(pair2);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void givenTwoPairsWithDifferentFirstValues_whenEquals_thenReturnsFalse() {
        // Given
        Pair<String, Integer> pair1 = new Pair<>("hello", 42);
        Pair<String, Integer> pair2 = new Pair<>("world", 42);

        // When
        boolean result = pair1.equals(pair2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void givenTwoPairsWithDifferentSecondValues_whenEquals_thenReturnsFalse() {
        // Given
        Pair<String, Integer> pair1 = new Pair<>("hello", 42);
        Pair<String, Integer> pair2 = new Pair<>("hello", 100);

        // When
        boolean result = pair1.equals(pair2);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void givenPairComparedToItself_whenEquals_thenReturnsTrue() {
        // Given
        Pair<String, Integer> pair = new Pair<>("hello", 42);

        // When
        boolean result = pair.equals(pair);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void givenPairComparedToNonPair_whenEquals_thenReturnsFalse() {
        // Given
        Pair<String, Integer> pair = new Pair<>("hello", 42);

        // When
        boolean result = pair.equals("not a pair");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void givenTwoPairsWithSameValues_whenHashCode_thenReturnsSameHashCode() {
        // Given
        Pair<String, Integer> pair1 = new Pair<>("hello", 42);
        Pair<String, Integer> pair2 = new Pair<>("hello", 42);

        // When & Then
        assertThat(pair1.hashCode()).isEqualTo(pair2.hashCode());
    }
    
    public void givenTwoPairsWithSameValues_whenBooleanEqual_thenBooleanIsTrue() {
        // Given
        Pair<String, Integer> pair1 = new Pair<>("hello", 42);
        Pair<String, Integer> pair2 = new Pair<>("hello", 42);
        boolean b = (pair1 == pair2);
        
        // When & Then
        assertThat(b).isTrue();
    }

    @Test
    public void givenTwoPairsWithDifferentValues_whenHashCode_thenReturnsDifferentHashCodes() {
        // Given
        Pair<String, Integer> pair1 = new Pair<>("hello", 42);
        Pair<String, Integer> pair2 = new Pair<>("world", 100);

        // When & Then
        assertThat(pair1.hashCode()).isNotEqualTo(pair2.hashCode());
    }

    @Test
    public void givenListOfPairs_whenExtractFirstsFromList_thenReturnsFirstElements() {
        // Given
        List<Pair<String, Integer>> pairs = Arrays.asList(
            new Pair<>("hello", 1),
            new Pair<>("world", 2),
            new Pair<>("foo", 3)
        );

        // When
        List<String> firsts = Pair.extractFirstsFromList(pairs);

        // Then
        assertThat(firsts).containsExactly("hello", "world", "foo");
    }

    @Test
    public void givenListOfPairs_whenExtractSecondsFromList_thenReturnsSecondElements() {
        // Given
        List<Pair<String, Integer>> pairs = Arrays.asList(
            new Pair<>("hello", 1),
            new Pair<>("world", 2),
            new Pair<>("foo", 3)
        );

        // When
        List<Integer> seconds = Pair.extractSecondsFromList(pairs);

        // Then
        assertThat(seconds).containsExactly(1, 2, 3);
    }

    @Test
    public void givenEmptyList_whenExtractFirstsFromList_thenReturnsEmptyList() {
        // Given
        List<Pair<String, Integer>> pairs = Arrays.asList();

        // When
        List<String> firsts = Pair.extractFirstsFromList(pairs);

        // Then
        assertThat(firsts).isEmpty();
    }

    @Test
    public void givenEmptyList_whenExtractSecondsFromList_thenReturnsEmptyList() {
        // Given
        List<Pair<String, Integer>> pairs = Arrays.asList();

        // When
        List<Integer> seconds = Pair.extractSecondsFromList(pairs);

        // Then
        assertThat(seconds).isEmpty();
    }
}
