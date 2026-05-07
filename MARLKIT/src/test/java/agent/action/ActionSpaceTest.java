package agent.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.testng.annotations.Test;

public class ActionSpaceTest {

    @Test
    public void givenDefaultActionSpace_whenGetActions_thenReturnEmptyList() {
        // Given
        ActionSpace actionSpace = new ActionSpace();

        // When
        int size = actionSpace.getActions().size();

        // Then
        assertThat(size).isZero();
    }

    @Test
    public void givenMutableActionSpace_whenAddAction_thenActionIsStored() {
        // Given
        ActionSpace actionSpace = new ActionSpace(new ArrayList<>());
        Action action = new DummyAction("a");

        // When
        actionSpace.addAction(action);

        // Then
        assertThat(actionSpace.getActions()).containsExactly(action);
    }


    @Test
    public void givenActionSpaceWithTwoActions_whenRemoveAtIndex_thenRemoveThatAction() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");
        ActionSpace actionSpace = new ActionSpace(new ArrayList<>());
        actionSpace.addAction(action1);
        actionSpace.addAction(action2);

        // When
        actionSpace.removeAtIndex(0);

        // Then
        assertThat(actionSpace.getActions()).containsExactly(action2);
    }

    @Test
    public void givenActions_whenOf_thenReturnJointActionWithSameActions() {
        // Given
        Action action1 = new DummyAction("a1");
        Action action2 = new DummyAction("a2");

        // When
        JointAction jointAction = ActionSpace.of(action1, action2);

        // Then
        assertThat(jointAction.getActions()).containsExactly(action1, action2);
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
