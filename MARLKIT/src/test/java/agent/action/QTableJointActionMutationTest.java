package agent.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import learning.policy.PolicyInput;
import learning.policy.valuefunction.QTable;
import util.Pair;

public class QTableJointActionMutationTest {
    @Test
    public void givenJointActionKey_whenGetBeforeMutation_thenReturnStoredValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Action a1 = new DummyAction();
        PolicyInput input = new DummyPolicyInput();

        JointAction ja = new JointAction();
        ja.addAction(a1);

        Pair<PolicyInput, Action> key = new Pair<>(input, ja);
        qTable.setValue(key, 42.0);

        // When
        double value = qTable.getValue(key);

        // Then
        assertThat(value).isEqualTo(42.0);
    }

    @Test
    public void givenMutatedJointActionKey_whenGetWithSamePair_thenReturnDefaultValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Action a1 = new DummyAction();
        Action a2 = new DummyAction();
        PolicyInput input = new DummyPolicyInput();

        JointAction ja = new JointAction();
        ja.addAction(a1); 

        Pair<PolicyInput, Action> key = new Pair<>(input, ja);
        qTable.setValue(key, 42.0);

        ja.addAction(a2);

        // When
        double value = qTable.getValue(key);

        // Then
        assertThat(value).isEqualTo(0.0);
    }

    @Test
    public void givenMutatedStoredJointAction_whenGetWithFreshEquivalentOldKey_thenReturnDefaultValue() {
        // Given
        QTable qTable = new QTable(0.0);

        Action a1 = new DummyAction();
        Action a2 = new DummyAction();
        PolicyInput input = new DummyPolicyInput();

        JointAction ja = new JointAction();
        ja.addAction(a1); 

        Pair<PolicyInput, Action> key = new Pair<>(input, ja);
        qTable.setValue(key, 42.0);

        ja.addAction(a2); 

        JointAction ja2 = new JointAction();
        ja2.addAction(a1);

        Pair<PolicyInput, Action> key2 = new Pair<>(input, ja2);

        // When
        double value = qTable.getValue(key2);

        // Then
        assertThat(value).isEqualTo(42.0);
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

		@Override
		public PolicyInput copy() {
			return null;
		}
    }
}