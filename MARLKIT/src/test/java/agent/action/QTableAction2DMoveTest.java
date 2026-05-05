package agent.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import learning.policy.PolicyInput;
import learning.policy.valuefunction.QTable;
import util.Pair;

public class QTableAction2DMoveTest {

    @Test
    public void givenAction2DMoveKey_whenGetBeforeMutation_thenReturnStoredValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Move2D action = new Move2D(new Pair<>(1, 0));
        PolicyInput input = new DummyPolicyInput();

        Pair<PolicyInput, Action> key = new Pair<>(input, action);
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

        Move2D action = new Move2D(new Pair<>(1, 0));
        PolicyInput input = new DummyPolicyInput();

        Pair<PolicyInput, Action> key = new Pair<>(input, action);
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

        Move2D action1 = Move2D.left();
        Move2D action2 = Move2D.left();
        PolicyInput input = new DummyPolicyInput();

        Pair<PolicyInput, Action> key = new Pair<>(input, action1);
        Pair<PolicyInput, Action> key2 = new Pair<>(input, action2);
        
        qTable.setValue(key, 42.0);
        action1.add(Move2D.right());

        // When
        double value1 = qTable.getValue(key);
        double value2 = qTable.getValue(key2);

        // Then
        assertThat(action1).isNotEqualTo(action2);
        assertThat(key).isNotEqualTo(key2);
        assertThat(value1).isEqualTo(0.0);
        assertThat(value2).isEqualTo(42.0);
    }

    private static class DummyPolicyInput implements PolicyInput {
        @Override
        public PolicyInput add(PolicyInput other) {
            return null;
        }
        
        public boolean equals(Object o) {
			return this == o || (o != null && getClass() == o.getClass());
		}
        
        public int hashCode() {
        	return getClass().hashCode();
        }

		@Override
		public PolicyInput copy() {
			return new DummyPolicyInput();
		}
    }
}