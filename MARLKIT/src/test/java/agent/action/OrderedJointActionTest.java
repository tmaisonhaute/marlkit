package agent.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class OrderedJointActionTest {

    @Test
    public void givenEmptyJointAction_whenAddAction_thenSizeIsOne() {
        // Given
    	OrderedJointAction jointAction = new OrderedJointAction();
        Action action = new DummyAction("a");

        // When
        jointAction.addAction(action);

        // Then
        assertThat(jointAction.size()).isEqualTo(1);
    }

    @Test
    public void givenJointActionWithActions_whenGetActionAtIndex_thenReturnExpectedAction() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        OrderedJointAction jointAction = OrderedJointAction.of(action1, action2);

        // When
        Action actionAtIndex = jointAction.getActionAtIndex(1);

        // Then
        assertThat(actionAtIndex).isEqualTo(action2);
    }

    @Test
    public void givenJointActionWithActions_whenRemoveActionAtIndex_thenRemoveThatAction() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        OrderedJointAction jointAction = new OrderedJointAction();
        jointAction.addAction(action1);
        jointAction.addAction(action2);

        // When
        jointAction.removeActionAtIndex(0);

        // Then
        assertThat(jointAction.getActions()).containsExactly(action2);
    }

    @Test
    public void givenJointActionWithActions_whenWithActionFirst_thenReturnNewJointActionWithActionAtBeginning() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        Action first = new DummyAction("first");
        OrderedJointAction jointAction = OrderedJointAction.of(action1, action2);

        // When
        OrderedJointAction newJointAction = jointAction.withActionFirst(first);

        // Then
        assertThat(newJointAction.getActions()).containsExactly(first, action1, action2);
    }

    @Test
    public void givenJointActionWithActions_whenWithActionAtIndexNegative_thenDoNotAddAction() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        Action inserted = new DummyAction("inserted");
        OrderedJointAction jointAction = OrderedJointAction.of(action1, action2);

        // When
        OrderedJointAction newJointAction = jointAction.withActionAtIndex(inserted, -1);

        // Then
        assertThat(newJointAction.getActions()).containsExactly(action1, action2);
    }

    @Test
    public void givenJointActionWithActions_whenWithActionAtIndexInRange_thenInsertAtIndex() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        Action inserted = new DummyAction("inserted");
        OrderedJointAction jointAction = OrderedJointAction.of(action1, action2);

        // When
        OrderedJointAction newJointAction = jointAction.withActionAtIndex(inserted, 1);

        // Then
        assertThat(newJointAction.getActions()).containsExactly(action1, inserted, action2);
    }

    @Test
    public void givenJointActionWithActions_whenWithActionAtIndexGreaterThanSize_thenAddAtEnd() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        Action inserted = new DummyAction("inserted");
        OrderedJointAction jointAction = OrderedJointAction.of(action1, action2);

        // When
        OrderedJointAction newJointAction = jointAction.withActionAtIndex(inserted, 5);

        // Then
        assertThat(newJointAction.getActions()).containsExactly(action1, action2, inserted);
    }

    @Test
    public void givenTwoJointActionWithSameActions_whenEquals_thenReturnTrue() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        OrderedJointAction jointAction1 = OrderedJointAction.of(action1, action2);
        OrderedJointAction jointAction2 = OrderedJointAction.of(new DummyAction("a1"), new DummyAction("a2"));

        // When
        boolean isEqual = jointAction1.equals(jointAction2);

        // Then
        assertThat(isEqual).isTrue();
    }

    @Test
    public void givenJointAction_whenCopy_thenReturnDeepCopy() {
        // Given
        DummyAction action1 = new DummyAction("a1");
        DummyAction action2 = new DummyAction("a2");
        OrderedJointAction jointAction = OrderedJointAction.of(action1, action2);

        // When
        OrderedJointAction copy = (OrderedJointAction) jointAction.copy();

        // Then
        assertThat(copy.getActions()).containsExactly(new DummyAction("a1"), new DummyAction("a2"));
        assertThat(copy.getActionAtIndex(0)).isNotSameAs(action1);
        assertThat(copy.getActionAtIndex(1)).isNotSameAs(action2);
    }


    private static final class DummyAction implements Action {
        private final String name;

        private DummyAction(String name) {
            this.name = name;
        }

        @Override
        public Action copy() {
            return new DummyAction(name);
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
    }
}
