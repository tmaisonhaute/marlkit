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

        Action2DMove action = new Action2DMove(new Pair<>(1, 0));
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

        Action2DMove action = new Action2DMove(new Pair<>(1, 0));
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
    public void givenMutatedStoredAction2DMove_whenGetWithFreshEquivalentOldKey_thenReturnDefaultValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Action2DMove action1 = Action2DMove.left();
        Action2DMove action2 = Action2DMove.left();
        PolicyInput input = new DummyPolicyInput();

        Pair<PolicyInput, Action> key = new Pair<>(input, action1);
        Pair<PolicyInput, Action> key2 = new Pair<>(input, action2);
        
        qTable.setValue(key, 42.0);
        action1.add(Action2DMove.right());

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

		@Override
		public PolicyInput copy() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}