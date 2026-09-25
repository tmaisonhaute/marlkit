package agent.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.List;

import org.testng.annotations.Test;

import util.Pair;

public class Move2DDoubleTest {

    private static final double EPSILON = 1e-12;

    @Test
    public void givenPair_whenConstruct_thenComponentsAreStored() {
        // Given
        Pair<Double, Double> value = new Pair<>(1.5, -2.5);

        // When
        Move2DDouble move = new Move2DDouble(value);

        // Then
        assertThat(move.getFirst()).isEqualTo(1.5);
        assertThat(move.getSecond()).isEqualTo(-2.5);
        assertThat(move.getSize()).isEqualTo(2);
    }

    @Test
    public void givenComponents_whenConstruct_thenComponentsAreStored() {
        // Given & When
        Move2DDouble move = new Move2DDouble(1.5, -2.5);

        // Then
        assertThat(move.getFirst()).isEqualTo(1.5);
        assertThat(move.getSecond()).isEqualTo(-2.5);
        assertThat(move.getSize()).isEqualTo(2);
    }

    @Test
    public void givenNullPair_whenConstruct_thenNullPointerExceptionIsThrown() {
        // Given
        Pair<Double, Double> value = null;

        // When & Then
        assertThatThrownBy(() -> new Move2DDouble(value))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("value");
    }

    @Test
    public void givenMove_whenGetValue_thenEquivalentPairIsReturned() {
        // Given
        Move2DDouble move = new Move2DDouble(1.5, -2.5);

        // When
        Pair<Double, Double> value = move.getValue();

        // Then
        assertThat(value).isEqualTo(new Pair<>(1.5, -2.5));
    }

    @Test
    public void givenMove_whenGetValue_thenIndependentPairIsReturned() {
        // Given
        Move2DDouble move = new Move2DDouble(1.5, -2.5);

        // When
        Pair<Double, Double> value = move.getValue();
        value.setFirst(10.0);
        value.setSecond(20.0);

        // Then
        assertThat(move.getFirst()).isEqualTo(1.5);
        assertThat(move.getSecond()).isEqualTo(-2.5);
    }

    @Test
    public void givenPair_whenSetValue_thenBothComponentsAreReplaced() {
        // Given
        Move2DDouble move = new Move2DDouble(1.0, 2.0);
        Pair<Double, Double> value = new Pair<>(3.0, 4.0);

        // When
        move.setValue(value);

        // Then
        assertThat(move.getFirst()).isEqualTo(3.0);
        assertThat(move.getSecond()).isEqualTo(4.0);
    }

    @Test
    public void givenNullPair_whenSetValue_thenNullPointerExceptionIsThrownAndMoveRemainsUnchanged() {
        // Given
        Move2DDouble move = new Move2DDouble(1.0, 2.0);

        // When & Then
        assertThatThrownBy(() -> move.setValue((Pair<Double, Double>) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("value");

        assertThat(move.getFirst()).isEqualTo(1.0);
        assertThat(move.getSecond()).isEqualTo(2.0);
    }

    @Test
    public void givenMove_whenSetFirst_thenOnlyFirstComponentIsReplaced() {
        // Given
        Move2DDouble move = new Move2DDouble(1.0, 2.0);

        // When
        move.setFirst(3.0);

        // Then
        assertThat(move.getFirst()).isEqualTo(3.0);
        assertThat(move.getSecond()).isEqualTo(2.0);
    }

    @Test
    public void givenMove_whenSetSecond_thenOnlySecondComponentIsReplaced() {
        // Given
        Move2DDouble move = new Move2DDouble(1.0, 2.0);

        // When
        move.setSecond(3.0);

        // Then
        assertThat(move.getFirst()).isEqualTo(1.0);
        assertThat(move.getSecond()).isEqualTo(3.0);
    }

    @Test
    public void givenTwoMoves_whenAdd_thenOtherComponentsAreAddedInPlace() {
        // Given
        Move2DDouble move = new Move2DDouble(1.5, -2.0);
        Move2DDouble other = new Move2DDouble(2.5, 5.0);

        // When
        move.add(other);

        // Then
        assertThat(move.getFirst()).isEqualTo(4.0);
        assertThat(move.getSecond()).isEqualTo(3.0);
        assertThat(other.getFirst()).isEqualTo(2.5);
        assertThat(other.getSecond()).isEqualTo(5.0);
    }

    @Test
    public void givenNullMove_whenAdd_thenNullPointerExceptionIsThrownAndMoveRemainsUnchanged() {
        // Given
        Move2DDouble move = new Move2DDouble(1.0, 2.0);

        // When & Then
        assertThatThrownBy(() -> move.add((Move2DDouble) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("other");

        assertThat(move.getFirst()).isEqualTo(1.0);
        assertThat(move.getSecond()).isEqualTo(2.0);
    }

    @Test
    public void givenPair_whenAdd_thenPairComponentsAreAddedInPlace() {
        // Given
        Move2DDouble move = new Move2DDouble(1.5, -2.0);
        Pair<Double, Double> vector = new Pair<>(2.5, 5.0);

        // When
        move.add(vector);

        // Then
        assertThat(move.getFirst()).isEqualTo(4.0);
        assertThat(move.getSecond()).isEqualTo(3.0);
    }

    @Test
    public void givenNullPair_whenAdd_thenNullPointerExceptionIsThrownAndMoveRemainsUnchanged() {
        // Given
        Move2DDouble move = new Move2DDouble(1.0, 2.0);

        // When & Then
        assertThatThrownBy(() -> move.add((Pair<Double, Double>) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("vect");

        assertThat(move.getFirst()).isEqualTo(1.0);
        assertThat(move.getSecond()).isEqualTo(2.0);
    }

    @Test
    public void givenPositiveScalar_whenMultiply_thenBothComponentsAreMultiplied() {
        // Given
        Move2DDouble move = new Move2DDouble(2.0, -3.0);

        // When
        move.multiply(2.5);

        // Then
        assertThat(move.getFirst()).isEqualTo(5.0);
        assertThat(move.getSecond()).isEqualTo(-7.5);
    }

    @Test
    public void givenZeroScalar_whenMultiply_thenMoveBecomesIdle() {
        // Given
        Move2DDouble move = new Move2DDouble(2.0, -3.0);

        // When
        move.multiply(0.0);

        // Then
        assertThat(move).isEqualTo(Move2DDouble.idle());
    }

    @Test
    public void givenNegativeScalar_whenMultiply_thenDirectionIsReversed() {
        // Given
        Move2DDouble move = new Move2DDouble(2.0, -3.0);

        // When
        move.multiply(-2.0);

        // Then
        assertThat(move.getFirst()).isEqualTo(-4.0);
        assertThat(move.getSecond()).isEqualTo(6.0);
    }

    @Test
    public void givenThreeFourMove_whenNorm_thenReturnFive() {
        // Given
        Move2DDouble move = new Move2DDouble(3.0, 4.0);

        // When
        double norm = move.norm();

        // Then
        assertThat(norm).isEqualTo(5.0);
    }

    @Test
    public void givenIdleMove_whenNorm_thenReturnZero() {
        // Given
        Move2DDouble move = Move2DDouble.idle();

        // When
        double norm = move.norm();

        // Then
        assertThat(norm).isZero();
    }

    @Test
    public void givenNonZeroMove_whenNormalize_thenMoveHasUnitNormAndDirectionIsPreserved() {
        // Given
        Move2DDouble move = new Move2DDouble(3.0, 4.0);

        // When
        move.normalize();

        // Then
        assertThat(move.getFirst()).isCloseTo(0.6, within(EPSILON));
        assertThat(move.getSecond()).isCloseTo(0.8, within(EPSILON));
        assertThat(move.norm()).isCloseTo(1.0, within(EPSILON));
    }

    @Test
    public void givenIdleMove_whenNormalize_thenMoveRemainsIdle() {
        // Given
        Move2DDouble move = Move2DDouble.idle();

        // When
        move.normalize();

        // Then
        assertThat(move.getFirst()).isZero();
        assertThat(move.getSecond()).isZero();
        assertThat(move.norm()).isZero();
    }

    @Test
    public void givenNonZeroMove_whenNormalized_thenNormalizedIndependentCopyIsReturned() {
        // Given
        Move2DDouble move = new Move2DDouble(3.0, 4.0);

        // When
        Move2DDouble normalized = move.normalized();

        // Then
        assertThat(normalized).isNotSameAs(move);
        assertThat(normalized.getFirst()).isCloseTo(0.6, within(EPSILON));
        assertThat(normalized.getSecond()).isCloseTo(0.8, within(EPSILON));
        assertThat(normalized.norm()).isCloseTo(1.0, within(EPSILON));

        assertThat(move.getFirst()).isEqualTo(3.0);
        assertThat(move.getSecond()).isEqualTo(4.0);
        assertThat(move.norm()).isEqualTo(5.0);
    }

    @Test
    public void givenIdleMove_whenNormalized_thenIndependentIdleCopyIsReturned() {
        // Given
        Move2DDouble move = Move2DDouble.idle();

        // When
        Move2DDouble normalized = move.normalized();

        // Then
        assertThat(normalized).isNotSameAs(move);
        assertThat(normalized).isEqualTo(move);
    }

    @Test
    public void givenZeroAngleAndPositiveSpeed_whenFromAngle_thenMovePointsRight() {
        // Given
        double angle = 0.0;
        double speed = 2.0;

        // When
        Move2DDouble move = Move2DDouble.fromAngle(angle, speed);

        // Then
        assertThat(move.getFirst()).isCloseTo(2.0, within(EPSILON));
        assertThat(move.getSecond()).isCloseTo(0.0, within(EPSILON));
        assertThat(move.norm()).isCloseTo(2.0, within(EPSILON));
    }

    @Test
    public void givenHalfPiAngleAndPositiveSpeed_whenFromAngle_thenMovePointsUp() {
        // Given
        double angle = Math.PI / 2.0;
        double speed = 2.0;

        // When
        Move2DDouble move = Move2DDouble.fromAngle(angle, speed);

        // Then
        assertThat(move.getFirst()).isCloseTo(0.0, within(EPSILON));
        assertThat(move.getSecond()).isCloseTo(2.0, within(EPSILON));
        assertThat(move.norm()).isCloseTo(2.0, within(EPSILON));
    }

    @Test
    public void givenZeroSpeed_whenFromAngle_thenIdleMoveIsReturned() {
        // Given
        double angle = Math.PI / 3.0;

        // When
        Move2DDouble move = Move2DDouble.fromAngle(angle, 0.0);

        // Then
        assertThat(move.norm()).isCloseTo(0.0, within(EPSILON));
    }

    @Test
    public void givenNonZeroVectorAndSpeed_whenFromVector_thenDirectionIsPreservedAndNormMatchesSpeed() {
        // Given
        double dx = 3.0;
        double dy = 4.0;
        double speed = 10.0;

        // When
        Move2DDouble move = Move2DDouble.fromVector(dx, dy, speed);

        // Then
        assertThat(move.getFirst()).isCloseTo(6.0, within(EPSILON));
        assertThat(move.getSecond()).isCloseTo(8.0, within(EPSILON));
        assertThat(move.norm()).isCloseTo(10.0, within(EPSILON));
    }

    @Test
    public void givenZeroVector_whenFromVector_thenIdleMoveIsReturned() {
        // Given & When
        Move2DDouble move = Move2DDouble.fromVector(0.0, 0.0, 10.0);

        // Then
        assertThat(move).isEqualTo(Move2DDouble.idle());
    }

    @Test
    public void givenZeroSpeed_whenFromVector_thenIdleMoveIsReturned() {
        // Given & When
        Move2DDouble move = Move2DDouble.fromVector(3.0, 4.0, 0.0);

        // Then
        assertThat(move.getFirst()).isCloseTo(0.0, within(EPSILON));
        assertThat(move.getSecond()).isCloseTo(0.0, within(EPSILON));
    }

    @Test
    public void givenFourDirections_whenGetDirectionalMoves_thenCardinalDirectionsAreReturnedInOrder() {
        // Given & When
        List<Action> actions = Move2DDouble.getDirectionalMoves(4, 1.0);

        // Then
        assertThat(actions).hasSize(4);

        assertMove(actions.get(0), 1.0, 0.0);
        assertMove(actions.get(1), 0.0, 1.0);
        assertMove(actions.get(2), -1.0, 0.0);
        assertMove(actions.get(3), 0.0, -1.0);
    }

    @Test
    public void givenDirectionsAndSpeed_whenGetDirectionalMoves_thenAllMovesHaveRequestedNorm() {
        // Given
        int numberOfDirections = 16;
        double speed = 0.2;

        // When
        List<Action> actions = Move2DDouble.getDirectionalMoves(numberOfDirections, speed);

        // Then
        assertThat(actions).hasSize(numberOfDirections);

        for (Action action : actions) {
            assertThat(action).isInstanceOf(Move2DDouble.class);
            assertThat(((Move2DDouble) action).norm()).isCloseTo(speed, within(EPSILON));
        }
    }

    @Test
    public void givenOneDirection_whenGetDirectionalMoves_thenSingleRightMoveIsReturned() {
        // Given & When
        List<Action> actions = Move2DDouble.getDirectionalMoves(1, 2.0);

        // Then
        assertThat(actions).hasSize(1);
        assertMove(actions.get(0), 2.0, 0.0);
    }

    @Test
    public void givenZeroDirections_whenGetDirectionalMoves_thenIllegalArgumentExceptionIsThrown() {
        // Given & When & Then
        assertThatThrownBy(() -> Move2DDouble.getDirectionalMoves(0, 1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("numberOfDirections");
    }

    @Test
    public void givenNegativeDirections_whenGetDirectionalMoves_thenIllegalArgumentExceptionIsThrown() {
        // Given & When & Then
        assertThatThrownBy(() -> Move2DDouble.getDirectionalMoves(-1, 1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("numberOfDirections");
    }

    @Test
    public void givenFourDirections_whenGetDirectionalMovesWithIdle_thenIdleIsAppended() {
        // Given & When
        List<Action> actions = Move2DDouble.getDirectionalMovesWithIdle(4, 1.0);

        // Then
        assertThat(actions).hasSize(5);

        assertMove(actions.get(0), 1.0, 0.0);
        assertMove(actions.get(1), 0.0, 1.0);
        assertMove(actions.get(2), -1.0, 0.0);
        assertMove(actions.get(3), 0.0, -1.0);
        assertMove(actions.get(4), 0.0, 0.0);
    }

    @Test
    public void givenMove_whenToString_thenComponentsAreIncluded() {
        // Given
        Move2DDouble move = new Move2DDouble(1.5, -2.5);

        // When
        String result = move.toString();

        // Then
        assertThat(result).isEqualTo("Move2DDouble [dx=1.5; dy=-2.5]");
    }

    @Test
    public void givenUpFactory_whenCalled_thenUnitUpMoveIsReturned() {
        // Given & When
        Move2DDouble move = Move2DDouble.up();

        // Then
        assertThat(move.getFirst()).isZero();
        assertThat(move.getSecond()).isEqualTo(1.0);
    }

    @Test
    public void givenDownFactory_whenCalled_thenUnitDownMoveIsReturned() {
        // Given & When
        Move2DDouble move = Move2DDouble.down();

        // Then
        assertThat(move.getFirst()).isZero();
        assertThat(move.getSecond()).isEqualTo(-1.0);
    }

    @Test
    public void givenLeftFactory_whenCalled_thenUnitLeftMoveIsReturned() {
        // Given & When
        Move2DDouble move = Move2DDouble.left();

        // Then
        assertThat(move.getFirst()).isEqualTo(-1.0);
        assertThat(move.getSecond()).isZero();
    }

    @Test
    public void givenRightFactory_whenCalled_thenUnitRightMoveIsReturned() {
        // Given & When
        Move2DDouble move = Move2DDouble.right();

        // Then
        assertThat(move.getFirst()).isEqualTo(1.0);
        assertThat(move.getSecond()).isZero();
    }

    @Test
    public void givenIdleFactory_whenCalled_thenZeroMoveIsReturned() {
        // Given & When
        Move2DDouble move = Move2DDouble.idle();

        // Then
        assertThat(move.getFirst()).isZero();
        assertThat(move.getSecond()).isZero();
    }

    @Test
    public void givenRepeatedFactoryCalls_whenCalled_thenIndependentMovesAreReturned() {
        // Given
        Move2DDouble firstMove = Move2DDouble.up();
        Move2DDouble secondMove = Move2DDouble.up();

        // When
        firstMove.setSecond(10.0);

        // Then
        assertThat(firstMove).isNotSameAs(secondMove);
        assertThat(firstMove.getSecond()).isEqualTo(10.0);
        assertThat(secondMove.getSecond()).isEqualTo(1.0);
    }

    @Test
    public void givenVonNeumannMoves_whenGetVonNeumannMove_thenFourDirectionsAreReturnedInExpectedOrder() {
        // Given & When
        List<Action> actions = Move2DDouble.getVonNeumannMove();

        // Then
        assertThat(actions).hasSize(4);

        assertMove(actions.get(0), 0.0, 1.0);
        assertMove(actions.get(1), 0.0, -1.0);
        assertMove(actions.get(2), -1.0, 0.0);
        assertMove(actions.get(3), 1.0, 0.0);
    }

    @Test
    public void givenVonNeumannMovesWithIdle_whenCalled_thenIdleIsAppended() {
        // Given & When
        List<Action> actions = Move2DDouble.getVonNeumannMoveWithIdle();

        // Then
        assertThat(actions).hasSize(5);

        assertMove(actions.get(0), 0.0, 1.0);
        assertMove(actions.get(1), 0.0, -1.0);
        assertMove(actions.get(2), -1.0, 0.0);
        assertMove(actions.get(3), 1.0, 0.0);
        assertMove(actions.get(4), 0.0, 0.0);
    }

    @Test
    public void givenTwoMoves_whenStaticAdd_thenSumIsReturnedWithoutMutatingOperands() {
        // Given
        Move2DDouble firstMove = new Move2DDouble(1.0, 2.0);
        Move2DDouble secondMove = new Move2DDouble(3.0, 4.0);

        // When
        Move2DDouble result = Move2DDouble.add(firstMove, secondMove);

        // Then
        assertThat(result.getFirst()).isEqualTo(4.0);
        assertThat(result.getSecond()).isEqualTo(6.0);

        assertThat(firstMove.getFirst()).isEqualTo(1.0);
        assertThat(firstMove.getSecond()).isEqualTo(2.0);
        assertThat(secondMove.getFirst()).isEqualTo(3.0);
        assertThat(secondMove.getSecond()).isEqualTo(4.0);
    }

    @Test
    public void givenNullFirstMove_whenStaticAdd_thenNullPointerExceptionIsThrown() {
        // Given
        Move2DDouble secondMove = Move2DDouble.right();

        // When & Then
        assertThatThrownBy(() -> Move2DDouble.add(null, secondMove))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("a");
    }

    @Test
    public void givenNullSecondMove_whenStaticAdd_thenNullPointerExceptionIsThrown() {
        // Given
        Move2DDouble firstMove = Move2DDouble.right();

        // When & Then
        assertThatThrownBy(() -> Move2DDouble.add(firstMove, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("b");
    }

    @Test
    public void givenMoveWithBounds_whenCopy_thenEquivalentIndependentMoveAndBoundsAreReturned() {
        // Given
        Move2DDouble original = new Move2DDouble(1.0, 2.0);
        original.setBounds(-5.0, 5.0);

        // When
        Move2DDouble copy = original.copy();
        Move2DDouble copy2 = original.copy();
        copy2.setBounds(5.0, 10.0);

        // Then
        assertThat(copy).isNotSameAs(original);
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getLowerBound()).isEqualTo(-5.0);
        assertThat(copy.getUpperBound()).isEqualTo(5.0);
        
        assertThat(copy2.getLowerBound()).isEqualTo(5.0);
        assertThat(copy2.getUpperBound()).isEqualTo(10.0);
        assertThat(copy2.getFirst()).isEqualTo(5.0);

        copy.setFirst(10.0);
        copy2.setSecond(10.0);

        assertThat(original.getFirst()).isEqualTo(1.0);
        assertThat(copy.getFirst()).isEqualTo(5.0);
        assertThat(copy2.getSecond()).isEqualTo(10.0);
    }

    @Test
    public void givenTwoComponentContinuousAction_whenFromActionContinuousVector_thenComponentsAndBoundsAreCopied() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.5, -2.5 });
        action.setBounds(-3.0, 3.0);

        // When
        Move2DDouble move = Move2DDouble.fromActionContinuousVector(action);

        // Then
        assertThat(move.getFirst()).isEqualTo(1.5);
        assertThat(move.getSecond()).isEqualTo(-2.5);
        assertThat(move.getLowerBound()).isEqualTo(-3.0);
        assertThat(move.getUpperBound()).isEqualTo(3.0);
    }

    @Test
    public void givenContinuousAction_whenFromActionContinuousVector_thenIndependentMoveIsReturned() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.5, -2.5 });

        // When
        Move2DDouble move = Move2DDouble.fromActionContinuousVector(action);
        action.setValue(0, 10.0);

        // Then
        assertThat(action.getValue(0)).isEqualTo(10.0);
        assertThat(move.getFirst()).isEqualTo(1.5);
    }

    @Test
    public void givenOneComponentContinuousAction_whenFromActionContinuousVector_thenIllegalArgumentExceptionIsThrown() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0 });

        // When & Then
        assertThatThrownBy(() -> Move2DDouble.fromActionContinuousVector(action))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size 2");
    }

    @Test
    public void givenThreeComponentContinuousAction_whenFromActionContinuousVector_thenIllegalArgumentExceptionIsThrown() {
        // Given
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0, 2.0, 3.0 });

        // When & Then
        assertThatThrownBy(() -> Move2DDouble.fromActionContinuousVector(action))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size 2");
    }

    @Test
    public void givenNullContinuousAction_whenFromActionContinuousVector_thenNullPointerExceptionIsThrown() {
        // Given
        ActionContinuousVector action = null;

        // When & Then
        assertThatThrownBy(() -> Move2DDouble.fromActionContinuousVector(action))
                .isInstanceOf(NullPointerException.class);
    }

    private void assertMove(Action action, double expectedFirst, double expectedSecond) {
        assertThat(action).isInstanceOf(Move2DDouble.class);

        Move2DDouble move = (Move2DDouble) action;

        assertThat(move.getFirst()).isCloseTo(expectedFirst, within(EPSILON));
        assertThat(move.getSecond()).isCloseTo(expectedSecond, within(EPSILON));
    }
}