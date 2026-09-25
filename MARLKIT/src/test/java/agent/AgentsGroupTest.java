package agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class AgentsGroupTest {

    @Test
    public void givenNoAgent_whenConstructEmptyGroup_thenAgentsListIsEmpty() {
        // Given & When
        AgentsGroup group = new AgentsGroup();

        // Then
        assertThat(group.getAgents()).isNotNull();
        assertThat(group.getAgents()).isEmpty();
    }

    @Test
    public void givenAgentList_whenConstructGroup_thenAgentsAreStored() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        List<MLKAgent> agents = new ArrayList<>(List.of(firstAgent, secondAgent));

        // When
        AgentsGroup group = new AgentsGroup(agents);

        // Then
        assertThat(group.getAgents()).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void givenAgentList_whenConstructGroup_thenOriginalListReferenceIsStored() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        List<MLKAgent> agents = new ArrayList<>();
        agents.add(firstAgent);

        // When
        AgentsGroup group = new AgentsGroup(agents);

        // Then
        assertThat(group.getAgents()).isSameAs(agents);
    }

    @Test
    public void givenConstructedGroup_whenOriginalListIsModified_thenGroupIsModified() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        List<MLKAgent> agents = new ArrayList<>();
        agents.add(firstAgent);

        AgentsGroup group = new AgentsGroup(agents);

        // When
        agents.add(secondAgent);

        // Then
        assertThat(group.getAgents()).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void givenEmptyGroup_whenAddAgent_thenAgentIsStored() {
        // Given
        AgentsGroup group = new AgentsGroup();
        MLKAgent agent = mock(MLKAgent.class);

        // When
        group.addAgent(agent);

        // Then
        assertThat(group.getAgents()).containsExactly(agent);
    }

    @Test
    public void givenGroup_whenAddMultipleAgents_thenInsertionOrderIsPreserved() {
        // Given
        AgentsGroup group = new AgentsGroup();
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        MLKAgent thirdAgent = mock(MLKAgent.class);

        // When
        group.addAgent(firstAgent);
        group.addAgent(secondAgent);
        group.addAgent(thirdAgent);

        // Then
        assertThat(group.getAgents()).containsExactly(firstAgent, secondAgent, thirdAgent);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenGroup_whenAddSameAgentTwice_thenDuplicateIsStored() {
        // Given
        AgentsGroup group = new AgentsGroup();
        MLKAgent agent = mock(MLKAgent.class);

        // When
        group.addAgent(agent);
        group.addAgent(agent);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void givenGroup_whenAddNullAgent_thenNullIsStored() {
        // Given
        AgentsGroup group = new AgentsGroup();

        // When
        group.addAgent(null);

    }

    @Test
    public void givenGroup_whenReturnedListIsModified_thenInternalListIsModified() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        AgentsGroup group = new AgentsGroup();
        group.addAgent(firstAgent);

        // When
        List<MLKAgent> returnedAgents = group.getAgents();
        returnedAgents.add(secondAgent);

        // Then
        assertThat(group.getAgents()).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void givenGroupWithAgents_whenReturnedListIsCleared_thenGroupBecomesEmpty() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        AgentsGroup group = new AgentsGroup();
        group.addAgent(firstAgent);
        group.addAgent(secondAgent);

        // When
        group.getAgents().clear();

        // Then
        assertThat(group.getAgents()).isEmpty();
    }

    @Test
    public void givenGroup_whenComparedWithItself_thenEqualsReturnsTrue() {
        // Given
        AgentsGroup group = new AgentsGroup();
        group.addAgent(mock(MLKAgent.class));

        // When
        boolean equal = group.equals(group);

        // Then
        assertThat(equal).isTrue();
    }

    @Test
    public void givenGroup_whenComparedWithNull_thenEqualsReturnsFalse() {
        // Given
        AgentsGroup group = new AgentsGroup();

        // When
        boolean equal = group.equals(null);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenGroup_whenComparedWithDifferentType_thenEqualsReturnsFalse() {
        // Given
        AgentsGroup group = new AgentsGroup();
        List<MLKAgent> agents = new ArrayList<>();

        // When
        boolean equal = group.equals(agents);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenTwoEmptyGroups_whenCompared_thenEqualsReturnsTrue() {
        // Given
        AgentsGroup firstGroup = new AgentsGroup();
        AgentsGroup secondGroup = new AgentsGroup();

        // When
        boolean equal = firstGroup.equals(secondGroup);

        // Then
        assertThat(equal).isTrue();
    }

    @Test
    public void givenGroupsContainingSameAgentInstancesInSameOrder_whenCompared_thenEqualsReturnsTrue() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        AgentsGroup firstGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));
        AgentsGroup secondGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));

        // When
        boolean equal = firstGroup.equals(secondGroup);

        // Then
        assertThat(equal).isTrue();
    }

    @Test
    public void givenGroupsContainingDifferentAgents_whenCompared_thenEqualsReturnsFalse() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        AgentsGroup firstGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent)));
        AgentsGroup secondGroup = new AgentsGroup(new ArrayList<>(List.of(secondAgent)));

        // When
        boolean equal = firstGroup.equals(secondGroup);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenGroupsContainingSameAgentsInDifferentOrder_whenCompared_thenEqualsReturnsFalse() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        AgentsGroup firstGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));
        AgentsGroup secondGroup = new AgentsGroup(new ArrayList<>(List.of(secondAgent, firstAgent)));

        // When
        boolean equal = firstGroup.equals(secondGroup);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenGroupsWithDifferentSizes_whenCompared_thenEqualsReturnsFalse() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        AgentsGroup firstGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent)));
        AgentsGroup secondGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));

        // When
        boolean equal = firstGroup.equals(secondGroup);

        // Then
        assertThat(equal).isFalse();
    }

    @Test
    public void givenEqualGroups_whenComparedBothWays_thenEqualsIsSymmetric() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        AgentsGroup firstGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));
        AgentsGroup secondGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));

        // When
        boolean firstEqualsSecond = firstGroup.equals(secondGroup);
        boolean secondEqualsFirst = secondGroup.equals(firstGroup);

        // Then
        assertThat(firstEqualsSecond).isTrue();
        assertThat(secondEqualsFirst).isTrue();
    }

    @Test
    public void givenThreeEqualGroups_whenCompared_thenEqualsIsTransitive() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        AgentsGroup firstGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));
        AgentsGroup secondGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));
        AgentsGroup thirdGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));

        // When
        boolean firstEqualsSecond = firstGroup.equals(secondGroup);
        boolean secondEqualsThird = secondGroup.equals(thirdGroup);
        boolean firstEqualsThird = firstGroup.equals(thirdGroup);

        // Then
        assertThat(firstEqualsSecond).isTrue();
        assertThat(secondEqualsThird).isTrue();
        assertThat(firstEqualsThird).isTrue();
    }

    @Test
    public void givenEqualGroups_whenHashCodeCalled_thenHashCodesAreEqual() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        AgentsGroup firstGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));
        AgentsGroup secondGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent, secondAgent)));

        // When
        int firstHashCode = firstGroup.hashCode();
        int secondHashCode = secondGroup.hashCode();

        // Then
        assertThat(firstHashCode).isEqualTo(secondHashCode);
    }

    @Test
    public void givenUnmodifiedGroup_whenHashCodeCalledMultipleTimes_thenHashCodeIsStable() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);
        AgentsGroup group = new AgentsGroup(new ArrayList<>(List.of(agent)));

        // When
        int firstHashCode = group.hashCode();
        int secondHashCode = group.hashCode();

        // Then
        assertThat(firstHashCode).isEqualTo(secondHashCode);
    }

    @Test
    public void givenGroupUsedForEquality_whenAgentIsAdded_thenGroupIsNoLongerEqualToOriginalEquivalentGroup() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);

        AgentsGroup firstGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent)));
        AgentsGroup secondGroup = new AgentsGroup(new ArrayList<>(List.of(firstAgent)));

        assertThat(firstGroup).isEqualTo(secondGroup);

        // When
        firstGroup.addAgent(secondAgent);

        // Then
        assertThat(firstGroup).isNotEqualTo(secondGroup);
    }

    @Test
    public void givenGroup_whenAgentsListIsMutated_thenHashCodeChangesAccordingToListContent() {
        // Given
        MLKAgent firstAgent = mock(MLKAgent.class);
        MLKAgent secondAgent = mock(MLKAgent.class);
        AgentsGroup group = new AgentsGroup(new ArrayList<>(List.of(firstAgent)));

        int initialHashCode = group.hashCode();

        // When
        group.addAgent(secondAgent);
        int updatedHashCode = group.hashCode();

        // Then
        assertThat(updatedHashCode).isNotEqualTo(initialHashCode);
    }
}