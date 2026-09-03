package agent.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import environment.observation.Observation;
import learning.policies.valuefunction.QTable;
import util.Pair;

public class QTableAction2DMoveTest {

    @Test
    public void givenAction2DMoveKey_whenGetBeforeMutation_thenReturnStoredValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Move2DInt action = new Move2DInt(new Pair<>(1, 0));
        Observation input = new DummyObservation();

        Pair<Observation, Action> key = new Pair<>(input, action);
        qTable.setValue(key, 42.0);

        // When
        double value = qTable.getValue(key);

        // Then
        assertThat(value).isEqualTo(42.0);
    }

    @Test
    public void givenMutatedAction2DMoveKey_whenGetWithSamePair_thenReturnDefaultValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Move2DInt action = new Move2DInt(new Pair<>(1, 0));
        Observation input = new DummyObservation();

        Pair<Observation, Action> key = new Pair<>(input, action);
        qTable.setValue(key, 42.0);

        action.add(new Pair<>(1, 0));

        // When
        double value = qTable.getValue(key);

        // Then
        assertThat(value).isEqualTo(0.0);
    }

    @Test
    public void givenMutatedStoredAction2DMove_whenGetWithFreshEquivalentOldKey_thenReturnStoredValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Move2DInt action1 = Move2DInt.left();
        Move2DInt action2 = Move2DInt.left();
        Observation input = new DummyObservation();

        Pair<Observation, Action> key = new Pair<>(input, action1);
        Pair<Observation, Action> key2 = new Pair<>(input, action2);
        
        qTable.setValue(key, 42.0);
        action1.add(Move2DInt.right());

        // When
        double value1 = qTable.getValue(key);
        double value2 = qTable.getValue(key2);

        // Then
        assertThat(action1).isNotEqualTo(action2);
        assertThat(key).isNotEqualTo(key2);
        assertThat(value1).isEqualTo(0.0);
        assertThat(value2).isEqualTo(42.0);
    }

    private static class DummyObservation implements Observation {
        @Override
        public Observation add(Observation other) {
            return null;
        }
        
        public boolean equals(Object o) {
			return this == o || (o != null && getClass() == o.getClass());
		}
        
        public int hashCode() {
        	return getClass().hashCode();
        }

		@Override
		public Observation copy() {
			return new DummyObservation();
		}
    }
}