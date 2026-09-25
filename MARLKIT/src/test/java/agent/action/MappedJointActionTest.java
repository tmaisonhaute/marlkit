package agent.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import agent.MLKAgent;

public class MappedJointActionTest {

    @Test
    public void givenNoArguments_whenConstruct_thenJointActionIsEmpty() {
        // Given & When
        MappedJointAction jointAction = new MappedJointAction();

        // Then
        assertThat(jointAction.isEmpty()).isTrue();
        assertThat(jointAction.size()).isZero();
        assertThat(jointAction.getActions()).isEmpty();
        assertThat(jointAction.getMappedActions()).isEmpty();
    }

    @Test
    public void givenAgentActionMap_whenConstruct_thenAssociationsAreStored() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();

        Map<MLKAgent, Action> actions = new LinkedHashMap<>();
        actions.put(firstAgent, firstAction);
        actions.put(secondAgent, secondAction);

        // When
        MappedJointAction jointAction = new MappedJointAction(actions);

        // Then
        assertThat(jointAction.size()).isEqualTo(2);
        assertThat(jointAction.getAction(firstAgent)).isSameAs(firstAction);
        assertThat(jointAction.getAction(secondAgent)).isSameAs(secondAction);
        assertThat(jointAction.getActions()).containsExactly(firstAction, secondAction);
    }

    @Test
    public void givenAgentActionMap_whenConstruct_thenMapIsCopied() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();

        Map<MLKAgent, Action> actions = new LinkedHashMap<>();
        actions.put(firstAgent, firstAction);

        // When
        MappedJointAction jointAction = new MappedJointAction(actions);
        actions.put(secondAgent, secondAction);

        // Then
        assertThat(jointAction.size()).isEqualTo(1);
        assertThat(jointAction.containsAgent(firstAgent)).isTrue();
        assertThat(jointAction.containsAgent(secondAgent)).isFalse();
    }

    @Test
    public void givenAgentActionMap_whenConstruct_thenActionsAreNotDeepCopied() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0, 2.0 });

        Map<MLKAgent, Action> actions = new LinkedHashMap<>();
        actions.put(agent, action);

        // When
        MappedJointAction jointAction = new MappedJointAction(actions);
        action.setValue(0, 10.0);

        // Then
        assertThat(jointAction.getAction(agent)).isSameAs(action);
        assertThat(((ActionContinuousVector) jointAction.getAction(agent)).getValue(0)).isEqualTo(10.0);
    }

    @Test
    public void givenNullMap_whenConstruct_thenNullPointerExceptionIsThrown() {
        // Given
        Map<MLKAgent, Action> actions = null;

        // When & Then
        assertThatThrownBy(() -> new MappedJointAction(actions))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenAgentActionMap_whenOf_thenEquivalentMappedJointActionIsReturned() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        Action firstAction = Move2DInt.up();
        Action secondAction = Move2DInt.down();

        Map<MLKAgent, Action> actions = new LinkedHashMap<>();
        actions.put(firstAgent, firstAction);
        actions.put(secondAgent, secondAction);

        // When
        MappedJointAction jointAction = MappedJointAction.of(actions);

        // Then
        assertThat(jointAction.getMappedActions()).containsEntry(firstAgent, firstAction);
        assertThat(jointAction.getMappedActions()).containsEntry(secondAgent, secondAction);
        assertThat(jointAction.getActions()).containsExactly(firstAction, secondAction);
    }

    @Test
    public void givenNullMap_whenOf_thenNullPointerExceptionIsThrown() {
        // Given
        Map<MLKAgent, Action> actions = null;

        // When & Then
        assertThatThrownBy(() -> MappedJointAction.of(actions))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenEmptyJointAction_whenAddAction_thenAssociationIsStored() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);
        Action action = Move2DInt.up();

        // When
        jointAction.addAction(agent, action);

        // Then
        assertThat(jointAction.size()).isEqualTo(1);
        assertThat(jointAction.isEmpty()).isFalse();
        assertThat(jointAction.containsAgent(agent)).isTrue();
        assertThat(jointAction.getAction(agent)).isSameAs(action);
    }

    @Test
    public void givenMultipleAgents_whenAddAction_thenInsertionOrderIsPreserved() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        MLKAgent thirdAgent = mock(MLKAgent.class);

        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();
        Action thirdAction = Move2DInt.up();

        // When
        jointAction.addAction(firstAgent, firstAction);
        jointAction.addAction(secondAgent, secondAction);
        jointAction.addAction(thirdAgent, thirdAction);

        // Then
        assertThat(jointAction.getActions()).containsExactly(firstAction, secondAction, thirdAction);
        assertThat(jointAction.getMappedActions().keySet()).containsExactly(firstAgent, secondAgent, thirdAgent);
    }

    @Test
    public void givenExistingAgent_whenAddAction_thenPreviousActionIsReplaced() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);
        Action initialAction = Move2DInt.left();
        Action replacementAction = Move2DInt.right();

        jointAction.addAction(agent, initialAction);

        // When
        jointAction.addAction(agent, replacementAction);

        // Then
        assertThat(jointAction.size()).isEqualTo(1);
        assertThat(jointAction.getAction(agent)).isSameAs(replacementAction);
        assertThat(jointAction.getActions()).containsExactly(replacementAction);
    }

    @Test
    public void givenExistingAgent_whenAddActionReplacesAction_thenAgentOrderIsPreserved() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        Action initialFirstAction = Move2DInt.left();
        Action replacementFirstAction = Move2DInt.up();
        Action secondAction = Move2DInt.right();

        jointAction.addAction(firstAgent, initialFirstAction);
        jointAction.addAction(secondAgent, secondAction);

        // When
        jointAction.addAction(firstAgent, replacementFirstAction);

        // Then
        assertThat(jointAction.getMappedActions().keySet()).containsExactly(firstAgent, secondAgent);
        assertThat(jointAction.getActions()).containsExactly(replacementFirstAction, secondAction);
    }

    @Test
    public void givenNullAgent_whenAddAction_thenNullPointerExceptionIsThrown() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        Action action = Move2DInt.up();

        // When & Then
        assertThatThrownBy(() -> jointAction.addAction(null, action))
                .isInstanceOf(NullPointerException.class);

        assertThat(jointAction.isEmpty()).isTrue();
    }

    @Test
    public void givenNullAction_whenAddAction_thenNullPointerExceptionIsThrown() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);

        // When & Then
        assertThatThrownBy(() -> jointAction.addAction(agent, null))
                .isInstanceOf(NullPointerException.class);

        assertThat(jointAction.isEmpty()).isTrue();
    }

    @Test
    public void givenJointAction_whenWithActionThenOriginalRemainsUnchanged() {
        // Given
        MappedJointAction original = new MappedJointAction();

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();

        original.addAction(firstAgent, firstAction);

        // When
        MappedJointAction result = original.withAction(secondAgent, secondAction);

        // Then
        assertThat(result).isNotSameAs(original);
        assertThat(original.size()).isEqualTo(1);
        assertThat(original.containsAgent(secondAgent)).isFalse();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.getAction(firstAgent)).isSameAs(firstAction);
        assertThat(result.getAction(secondAgent)).isSameAs(secondAction);
    }

    @Test
    public void givenExistingAgent_whenWithAction_thenActionIsReplacedOnlyInNewInstance() {
        // Given
        MappedJointAction original = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);
        Action originalAction = Move2DInt.left();
        Action replacementAction = Move2DInt.right();

        original.addAction(agent, originalAction);

        // When
        MappedJointAction result = original.withAction(agent, replacementAction);

        // Then
        assertThat(original.getAction(agent)).isSameAs(originalAction);
        assertThat(result.getAction(agent)).isSameAs(replacementAction);
        assertThat(original.size()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void givenJointAction_whenWithAction_thenExistingActionObjectsAreShared() {
        // Given
        MappedJointAction original = new MappedJointAction();
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        ActionContinuousVector firstAction = new ActionContinuousVector(new double[] { 1.0 });
        Action secondAction = Move2DInt.right();

        original.addAction(firstAgent, firstAction);

        // When
        MappedJointAction result = original.withAction(secondAgent, secondAction);
        firstAction.setValue(0, 10.0);

        // Then
        assertThat(original.getAction(firstAgent)).isSameAs(firstAction);
        assertThat(result.getAction(firstAgent)).isSameAs(firstAction);
        assertThat(((ActionContinuousVector) result.getAction(firstAgent)).getValue(0)).isEqualTo(10.0);
    }

    @Test
    public void givenStoredAgent_whenGetAction_thenAssociatedActionIsReturned() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);
        Action action = Move2DInt.down();

        jointAction.addAction(agent, action);

        // When
        Action returnedAction = jointAction.getAction(agent);

        // Then
        assertThat(returnedAction).isSameAs(action);
    }

    @Test
    public void givenUnknownAgent_whenGetAction_thenNullIsReturned() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent storedAgent = mock(MLKAgent.class);
        MLKAgent unknownAgent = mock(MLKAgent.class);

        jointAction.addAction(storedAgent, Move2DInt.left());

        // When
        Action action = jointAction.getAction(unknownAgent);

        // Then
        assertThat(action).isNull();
    }

    @Test
    public void givenStoredAgent_whenContainsAgent_thenTrueIsReturned() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);

        jointAction.addAction(agent, Move2DInt.up());

        // When
        boolean containsAgent = jointAction.containsAgent(agent);

        // Then
        assertThat(containsAgent).isTrue();
    }

    @Test
    public void givenUnknownAgent_whenContainsAgent_thenFalseIsReturned() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent storedAgent = mock(MLKAgent.class);
        MLKAgent unknownAgent = mock(MLKAgent.class);

        jointAction.addAction(storedAgent, Move2DInt.up());

        // When
        boolean containsAgent = jointAction.containsAgent(unknownAgent);

        // Then
        assertThat(containsAgent).isFalse();
    }

    @Test
    public void givenStoredAgent_whenRemoveAction_thenAssociationIsRemoved() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);

        jointAction.addAction(agent, Move2DInt.up());

        // When
        jointAction.removeAction(agent);

        // Then
        assertThat(jointAction.containsAgent(agent)).isFalse();
        assertThat(jointAction.getAction(agent)).isNull();
        assertThat(jointAction.isEmpty()).isTrue();
    }

    @Test
    public void givenUnknownAgent_whenRemoveAction_thenJointActionRemainsUnchanged() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent storedAgent = mock(MLKAgent.class);
        MLKAgent unknownAgent = mock(MLKAgent.class);
        Action action = Move2DInt.up();

        jointAction.addAction(storedAgent, action);

        // When
        jointAction.removeAction(unknownAgent);

        // Then
        assertThat(jointAction.size()).isEqualTo(1);
        assertThat(jointAction.getAction(storedAgent)).isSameAs(action);
    }

    @Test
    public void givenReturnedMappedActions_whenModified_thenJointActionIsNotModified() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        jointAction.addAction(firstAgent, Move2DInt.left());

        // When
        Map<MLKAgent, Action> returnedMap = jointAction.getMappedActions();
        returnedMap.put(secondAgent, Move2DInt.right());

        // Then
        assertThat(jointAction.containsAgent(firstAgent)).isTrue();
        assertThat(jointAction.containsAgent(secondAgent)).isFalse();
        assertThat(jointAction.size()).isEqualTo(1);
    }

    @Test
    public void givenReturnedMappedActions_whenCleared_thenJointActionIsUnchanged() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        jointAction.addAction(mock(MLKAgent.class), Move2DInt.left());
        jointAction.addAction(mock(MLKAgent.class), Move2DInt.right());

        // When
        jointAction.getMappedActions().clear();

        // Then
        assertThat(jointAction.isEmpty()).isFalse();
        assertThat(jointAction.size()).isEqualTo(2);
    }

    @Test
    public void givenJointAction_whenGetActions_thenActionsAreReturnedInInsertionOrder() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();

        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();
        Action thirdAction = Move2DInt.idle();

        jointAction.addAction(mock(MLKAgent.class), firstAction);
        jointAction.addAction(mock(MLKAgent.class), secondAction);
        jointAction.addAction(mock(MLKAgent.class), thirdAction);

        // When
        List<Action> actions = jointAction.getActions();

        // Then
        assertThat(actions).containsExactly(firstAction, secondAction, thirdAction);
    }

    @Test
    public void givenReturnedActionsList_whenModificationAttempted_thenUnsupportedOperationExceptionIsThrown() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        jointAction.addAction(mock(MLKAgent.class), Move2DInt.left());

        List<Action> actions = jointAction.getActions();

        // When & Then
        assertThatThrownBy(() -> actions.add(Move2DInt.right()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void givenReturnedActionsList_whenJointActionIsModified_thenReturnedListRemainsSnapshot() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();

        jointAction.addAction(mock(MLKAgent.class), firstAction);
        List<Action> actions = jointAction.getActions();

        // When
        jointAction.addAction(mock(MLKAgent.class), secondAction);

        // Then
        assertThat(actions).containsExactly(firstAction);
        assertThat(jointAction.getActions()).containsExactly(firstAction, secondAction);
    }

    @Test
    public void givenJointAction_whenCopy_thenEquivalentIndependentMappingIsReturned() {
        // Given
        MappedJointAction original = new MappedJointAction();

        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        original.addAction(firstAgent, Move2DInt.left());
        original.addAction(secondAgent, Move2DInt.right());

        // When
        MappedJointAction copy = original.copy();

        // Then
        assertThat(copy).isNotSameAs(original);
        assertThat(copy).isEqualTo(original);
        assertThat(copy.getMappedActions()).isNotSameAs(original.getMappedActions());
        assertThat(copy.getMappedActions().keySet()).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void givenJointAction_whenCopy_thenActionsAreDeepCopied() {
        // Given
        MappedJointAction original = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0, 2.0 });

        original.addAction(agent, action);

        // When
        MappedJointAction copy = original.copy();
        ActionContinuousVector copiedAction = (ActionContinuousVector) copy.getAction(agent);

        // Then
        assertThat(copiedAction).isNotSameAs(action);
        assertThat(copiedAction).isEqualTo(action);
    }

    @Test
    public void givenCopiedJointAction_whenOriginalActionIsMutated_thenCopiedActionRemainsUnchanged() {
        // Given
        MappedJointAction original = new MappedJointAction();
        MLKAgent agent = mock(MLKAgent.class);
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0, 2.0 });

        original.addAction(agent, action);
        MappedJointAction copy = original.copy();

        // When
        action.setValue(0, 100.0);

        // Then
        assertThat(((ActionContinuousVector) original.getAction(agent)).getValue(0)).isEqualTo(100.0);
        assertThat(((ActionContinuousVector) copy.getAction(agent)).getValue(0)).isEqualTo(1.0);
    }

    @Test
    public void givenCopiedJointAction_whenCopyMappingIsModified_thenOriginalMappingRemainsUnchanged() {
        // Given
        MappedJointAction original = new MappedJointAction();
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        original.addAction(firstAgent, Move2DInt.left());

        // When
        MappedJointAction copy = original.copy();
        copy.addAction(secondAgent, Move2DInt.right());

        // Then
        assertThat(original.size()).isEqualTo(1);
        assertThat(original.containsAgent(secondAgent)).isFalse();
        assertThat(copy.size()).isEqualTo(2);
    }

    @Test
    public void givenMapContainingNullAgent_whenConstruct_thenNullPointerExceptionIsThrown() {
        // Given
        Map<MLKAgent, Action> actions = new LinkedHashMap<>();
        actions.put(null, Move2DInt.up());

        // When & Then
        assertThatThrownBy(() -> new MappedJointAction(actions))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("agent");
    }
    
    @Test
    public void givenMapContainingNullAction_whenConstruct_thenNullPointerExceptionIsThrown() {
        // Given
        Map<MLKAgent, Action> actions = new LinkedHashMap<>();
        actions.put(mock(MLKAgent.class), null);

        // When & Then
        assertThatThrownBy(() -> new MappedJointAction(actions))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("action");
    }
    
    @Test
    public void givenMapContainingNullAgent_whenOf_thenNullPointerExceptionIsThrown() {
        // Given
        Map<MLKAgent, Action> actions = new LinkedHashMap<>();
        actions.put(null, Move2DInt.up());

        // When & Then
        assertThatThrownBy(() -> MappedJointAction.of(actions))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("agent");
    }
    
    @Test
    public void givenMapContainingNullAction_whenOf_thenNullPointerExceptionIsThrown() {
        // Given
        Map<MLKAgent, Action> actions = new LinkedHashMap<>();
        actions.put(mock(MLKAgent.class), null);

        // When & Then
        assertThatThrownBy(() -> MappedJointAction.of(actions))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("action");
    }
    
    @Test
    public void givenNullAgent_whenWithAction_thenNullPointerExceptionIsThrownAndOriginalRemainsUnchanged() {
        // Given
        MappedJointAction original = new MappedJointAction();
        MLKAgent storedAgent = mock(MLKAgent.class);
        Action storedAction = Move2DInt.left();

        original.addAction(storedAgent, storedAction);

        // When & Then
        assertThatThrownBy(() -> original.withAction(null, Move2DInt.right()))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("agent");

        assertThat(original.size()).isEqualTo(1);
        assertThat(original.getAction(storedAgent)).isSameAs(storedAction);
    }
    
    @Test
    public void givenNullAction_whenWithAction_thenNullPointerExceptionIsThrownAndOriginalRemainsUnchanged() {
        // Given
        MappedJointAction original = new MappedJointAction();
        MLKAgent storedAgent = mock(MLKAgent.class);
        MLKAgent addedAgent = mock(MLKAgent.class);
        Action storedAction = Move2DInt.left();

        original.addAction(storedAgent, storedAction);

        // When & Then
        assertThatThrownBy(() -> original.withAction(addedAgent, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("action");

        assertThat(original.size()).isEqualTo(1);
        assertThat(original.getAction(storedAgent)).isSameAs(storedAction);
        assertThat(original.containsAgent(addedAgent)).isFalse();
    }

    @Test
    public void givenJointAction_whenComparedWithItself_thenEqualsReturnsTrue() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();
        jointAction.addAction(mock(MLKAgent.class), Move2DInt.up());

        // When
        boolean equal = jointAction.equals(jointAction);

        // Then
        assertThat(equal).isTrue();
    }

    @Test
    public void givenJointAction_whenComparedWithNull_thenEqualsReturnsFalse() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();

        // When
        boolean equal = jointAction.equals(null);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenJointAction_whenComparedWithDifferentType_thenEqualsReturnsFalse() {
        // Given
        MappedJointAction jointAction = new MappedJointAction();

        // When
        boolean equal = jointAction.equals(Move2DInt.up());

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenEquivalentJointActions_whenCompared_thenEqualsReturnsTrue() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        MappedJointAction firstJointAction = new MappedJointAction();
        firstJointAction.addAction(firstAgent, Move2DInt.left());
        firstJointAction.addAction(secondAgent, Move2DInt.right());

        MappedJointAction secondJointAction = new MappedJointAction();
        secondJointAction.addAction(firstAgent, Move2DInt.left());
        secondJointAction.addAction(secondAgent, Move2DInt.right());

        // When
        boolean equal = firstJointAction.equals(secondJointAction);

        // Then
        assertThat(equal).isTrue();
    }

    @Test
    public void givenSameAssociationsInDifferentInsertionOrder_whenCompared_thenEqualsReturnsTrue() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();

        MappedJointAction firstJointAction = new MappedJointAction();
        firstJointAction.addAction(firstAgent, firstAction);
        firstJointAction.addAction(secondAgent, secondAction);

        MappedJointAction secondJointAction = new MappedJointAction();
        secondJointAction.addAction(secondAgent, secondAction);
        secondJointAction.addAction(firstAgent, firstAction);

        // When
        boolean equal = firstJointAction.equals(secondJointAction);

        // Then
        assertThat(firstJointAction.getActions()).containsExactly(firstAction, secondAction);
        assertThat(secondJointAction.getActions()).containsExactly(secondAction, firstAction);
        assertThat(equal).isTrue();
    }

    @Test
    public void givenDifferentAgentAssociations_whenCompared_thenEqualsReturnsFalse() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        MappedJointAction firstJointAction = new MappedJointAction();
        firstJointAction.addAction(firstAgent, Move2DInt.left());

        MappedJointAction secondJointAction = new MappedJointAction();
        secondJointAction.addAction(secondAgent, Move2DInt.left());

        // When
        boolean equal = firstJointAction.equals(secondJointAction);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenSameAgentWithDifferentActions_whenCompared_thenEqualsReturnsFalse() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);

        MappedJointAction firstJointAction = new MappedJointAction();
        firstJointAction.addAction(agent, Move2DInt.left());

        MappedJointAction secondJointAction = new MappedJointAction();
        secondJointAction.addAction(agent, Move2DInt.right());

        // When
        boolean equal = firstJointAction.equals(secondJointAction);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenEqualJointActions_whenHashCodeCalled_thenHashCodesAreEqual() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        MappedJointAction firstJointAction = new MappedJointAction();
        firstJointAction.addAction(firstAgent, Move2DInt.left());
        firstJointAction.addAction(secondAgent, Move2DInt.right());

        MappedJointAction secondJointAction = new MappedJointAction();
        secondJointAction.addAction(firstAgent, Move2DInt.left());
        secondJointAction.addAction(secondAgent, Move2DInt.right());

        // When
        int firstHashCode = firstJointAction.hashCode();
        int secondHashCode = secondJointAction.hashCode();

        // Then
        assertThat(firstHashCode).isEqualTo(secondHashCode);
    }

    @Test
    public void givenSameAssociationsInDifferentInsertionOrder_whenHashCodeCalled_thenHashCodesAreEqual() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        Action firstAction = Move2DInt.left();
        Action secondAction = Move2DInt.right();

        MappedJointAction firstJointAction = new MappedJointAction();
        firstJointAction.addAction(firstAgent, firstAction);
        firstJointAction.addAction(secondAgent, secondAction);

        MappedJointAction secondJointAction = new MappedJointAction();
        secondJointAction.addAction(secondAgent, secondAction);
        secondJointAction.addAction(firstAgent, firstAction);

        // When
        int firstHashCode = firstJointAction.hashCode();
        int secondHashCode = secondJointAction.hashCode();

        // Then
        assertThat(firstHashCode).isEqualTo(secondHashCode);
    }

    @Test
    public void givenMutableStoredAction_whenActionIsMutated_thenJointActionHashCodeCanChange() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);
        ActionContinuousVector action = new ActionContinuousVector(new double[] { 1.0 });
        MappedJointAction jointAction = new MappedJointAction();

        jointAction.addAction(agent, action);
        int initialHashCode = jointAction.hashCode();

        // When
        action.setValue(0, 2.0);
        int updatedHashCode = jointAction.hashCode();

        // Then
        assertThat(updatedHashCode).isNotEqualTo(initialHashCode);
    }

}



