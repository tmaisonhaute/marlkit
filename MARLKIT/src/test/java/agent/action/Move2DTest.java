package agent.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import util.Pair;

public class Move2DTest {

    @Test
    public void givenMove2D_whenGetFirstAndSecond_thenReturnComponents() {
        // Given
        Move2D move = new Move2D(new Pair<>(3, -2));

        // When
        int dx = move.getFirst();
        int dy = move.getSecond();

        // Then
        assertThat(new Pair<>(dx, dy)).isEqualTo(new Pair<>(3, -2));
    }

    @Test
    public void givenMove2D_whenSetValue_thenReturnUpdatedComponents() {
        // Given
        Move2D move = new Move2D(new Pair<>(0, 0));

        // When
        move.setValue(new Pair<>(5, 6));

        // Then
        assertThat(move.getValue()).isEqualTo(new Pair<>(5, 6));
    }

    @Test
    public void givenTwoEquivalentMove2D_whenEquals_thenReturnTrue() {
        // Given
        Move2D move1 = new Move2D(new Pair<>(1, 2));
        Move2D move2 = new Move2D(new Pair<>(1, 2));

        // When
        boolean isEqual = move1.equals(move2);

        // Then
        assertThat(isEqual).isTrue();
    }

    @Test
    public void givenMove2D_whenAddOtherMove2D_thenMutateThisMove() {
        // Given
        Move2D move = new Move2D(new Pair<>(1, 2));

        // When
        move.add(new Move2D(new Pair<>(3, 4)));

        // Then
        assertThat(move.getValue()).isEqualTo(new Pair<>(4, 6));
    }

    @Test
    public void givenMove2D_whenAddPair_thenMutateThisMove() {
        // Given
        Move2D move = new Move2D(new Pair<>(1, 2));

        // When
        move.add(new Pair<>(-2, 5));

        // Then
        assertThat(move.getValue()).isEqualTo(new Pair<>(-1, 7));
    }

    @Test
    public void givenTwoMoves_whenStaticAdd_thenReturnNewMoveAndDoNotMutateInputs() {
        // Given
        Move2D a = new Move2D(new Pair<>(1, 2));
        Move2D b = new Move2D(new Pair<>(3, 4));

        // When
        Move2D sum = Move2D.add(a, b);

        // Then
        assertThat(List.of(a.getValue(), b.getValue(), sum.getValue()))
                .containsExactly(new Pair<>(1, 2), new Pair<>(3, 4), new Pair<>(4, 6));
    }

    @Test
    public void givenDirectionFactories_whenCreate_thenReturnExpectedMoves() {
        // Given
        Move2D up = Move2D.up();
        Move2D down = Move2D.down();
        Move2D left = Move2D.left();
        Move2D right = Move2D.right();
        Move2D idle = Move2D.idle();

        // When
        List<Pair<Integer, Integer>> values = List.of(up.getValue(), down.getValue(), left.getValue(), right.getValue(), idle.getValue());

        // Then
        assertThat(values).containsExactly(new Pair<>(0, 1), new Pair<>(0, -1), new Pair<>(-1, 0), new Pair<>(1, 0), new Pair<>(0, 0));
    }

    @Test
    public void givenCallGetVonNeumannmove_whenInspect_thenReturnFourCardinalMoves() {
        // Given
        List<Action> moves = Move2D.getVonNeumannmove();

        // When
        List<Action> snapshot = moves;

        // Then
        assertThat(snapshot).containsExactly(Move2D.up(), Move2D.down(), Move2D.left(), Move2D.right());
    }

    @Test
    public void givenMove2D_whenCopy_thenReturnNewEquivalentInstance() {
        // Given
        Move2D move = new Move2D(new Pair<>(3, -2));

        // When
        Move2D copy = (Move2D) move.copy();

        // Then
        assertThat(copy).isEqualTo(move).isNotSameAs(move);
        assertThat(copy.getValue()).isEqualTo(move.getValue()).isNotSameAs(move.getValue());
    }
    
    @Test
    public void givenMoveAndItsCopy_whenUsedAsHashMapKey_thenBothAccessSameEntry() {
        // Given
        Move2D a1 = new Move2D(new Pair<>(1, 2));
        Move2D a2 = a1.copy();

        Map<Move2D, String> map = new HashMap<>();
        map.put(a1, "value");

        // Then
        assertThat(map).containsEntry(a2, "value");
    }
    
    @Test
    public void givenMoveUsedAsKey_whenMutated_thenItIsNoLongerFoundInHashMap() {
        // Given
        Move2D move = new Move2D(new Pair<>(1, 2));
        Map<Move2D, String> map = new HashMap<>();
        map.put(move, "value");

        // When
        move.add(new Pair<>(1, 0)); 

        // Then
        assertThat(map.get(move)).isNull();
    }
    
    @Test
    public void givenCopyStoredAsKey_whenOriginalMutates_thenCopyStillAccessesValue() {
        // Given
        Move2D a1 = new Move2D(new Pair<>(1, 2));
        Move2D a2 = a1.copy();

        Map<Move2D, String> map = new HashMap<>();
        map.put(a1.copy(), "value");

        // When
        a1.add(new Pair<>(1, 0));

        // Then
        assertThat(map.get(a1)).isNull();
        assertThat(map).containsEntry(a2, "value");
    }
    
    @Test
    public void givenMutableOriginalStoredAsKey_whenOriginalMutates_thenNeitherOriginalNorCopyCanAccessValue() {
        // Given
        Move2D a1 = new Move2D(new Pair<>(1, 2));
        Move2D a2 = a1.copy();

        Map<Move2D, String> map = new HashMap<>();
        map.put(a1, "value");

        // When
        a1.add(new Pair<>(1, 0));

        // Then
        assertThat(map.get(a1)).isNull();
        assertThat(map.get(a2)).isNull();
    }
    
}
