package agent.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import learning.policies.PolicyInput;
import learning.policies.valuefunction.QTable;
import util.Pair;

public class QTableActionTest {

    @Test
    public void givenActionKey_whenGetStoredValue_thenReturnStoredValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Action action = new DummyAction();
        PolicyInput input = new DummyPolicyInput();

        Pair<PolicyInput, Action> key = new Pair<>(input, action);
        qTable.setValue(key, 42.0);

        // When
        double value = qTable.getValue(key);

        // Then
        assertThat(value).isEqualTo(42.0);
    }

    @Test
    public void givenEquivalentActionKey_whenGetStoredValue_thenReturnStoredValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a1");
        PolicyInput input = new DummyPolicyInput();

        Pair<PolicyInput, Action> key1 = new Pair<>(input, action1);
        qTable.setValue(key1, 42.0);

        Pair<PolicyInput, Action> key2 = new Pair<>(input, action2);

        // When
        double value = qTable.getValue(key2);

        // Then
        assertThat(value).isEqualTo(42.0);
    }

    @Test
    public void givenDifferentActionKey_whenGetStoredValue_thenReturnDefaultValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        PolicyInput input = new DummyPolicyInput();

        Pair<PolicyInput, Action> key1 = new Pair<>(input, action1);
        qTable.setValue(key1, 42.0);

        Pair<PolicyInput, Action> key2 = new Pair<>(input, action2);

        // When
        double value = qTable.getValue(key2);

        // Then
        assertThat(value).isEqualTo(0.0);
    }

    private static class DummyAction implements Action {
        private final String name;

        private DummyAction() {
            this.name = "default";
        }

        private DummyAction(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof DummyAction other)) {
                return false;
            }
            return name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
        
		public DummyAction copy() {
			return new DummyAction(this.name);
		}
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