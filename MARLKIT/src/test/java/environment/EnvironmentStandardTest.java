package environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Action2DMove;
import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import environment.state.State;
import learning.Experience;
import util.Pair;
import util.Tuple;

public class EnvironmentStandardTest {
    
    private TestEnvironment environment;
    
    @BeforeMethod
    public void setUp() {
        environment = new TestEnvironment(10, 10);
    }
    
    @Test
    public void givenAgentWithObservationAndActionReward_whenCombineObsActReward_thenCorrectExperienceCreated() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);
        
        // Create observation
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        
        // Create action and reward
        Action action = Action2DMove.up();
        Reward reward = new RewardStandard(1.0);
        
        Map<MLKAgent, Observation> observationMap = new HashMap<>();
        observationMap.put(agent, observation);
        
        Map<MLKAgent, Pair<Action, Reward>> actionRewardMap = new HashMap<>();
        actionRewardMap.put(agent, new Pair<>(action, reward));
        
        // When
        Map<MLKAgent, Experience> result = environment.testCombineObsActReward(actionRewardMap, observationMap);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.containsKey(agent)).isTrue();
        
        Experience experience = result.get(agent);
        assertThat(experience.getObservation()).isEqualTo(observation);
        assertThat(experience.getAction()).isEqualTo(action);
        assertThat(experience.getReward()).isEqualTo(reward);
    }
    
    @Test
    public void givenMultipleAgents_whenCombineObsActReward_thenCorrectExperiencesCreated() {
        // Given
        MLKAgent agent1 = mock(MLKAgent.class);
        MLKAgent agent2 = mock(MLKAgent.class);
        
        // Create observations
        ObservationPositionsValues observation1 = new ObservationPositionsValues();
        observation1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        
        ObservationPositionsValues observation2 = new ObservationPositionsValues();
        observation2.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 2.0));
        
        // Create actions and rewards
        Action action1 = Action2DMove.up();
        Reward reward1 = new RewardStandard(1.0);
        
        Action action2 = Action2DMove.down();
        Reward reward2 = new RewardStandard(2.0);
        
        Map<MLKAgent, Observation> observationMap = new HashMap<>();
        observationMap.put(agent1, observation1);
        observationMap.put(agent2, observation2);
        
        Map<MLKAgent, Pair<Action, Reward>> actionRewardMap = new HashMap<>();
        actionRewardMap.put(agent1, new Pair<>(action1, reward1));
        actionRewardMap.put(agent2, new Pair<>(action2, reward2));
        
        // When
        Map<MLKAgent, Experience> result = environment.testCombineObsActReward(actionRewardMap, observationMap);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.containsKey(agent1)).isTrue();
        assertThat(result.containsKey(agent2)).isTrue();
        
        Experience experience1 = result.get(agent1);
        assertThat(experience1.getObservation()).isEqualTo(observation1);
        assertThat(experience1.getAction()).isEqualTo(action1);
        assertThat(experience1.getReward()).isEqualTo(reward1);
        
        Experience experience2 = result.get(agent2);
        assertThat(experience2.getObservation()).isEqualTo(observation2);
        assertThat(experience2.getAction()).isEqualTo(action2);
        assertThat(experience2.getReward()).isEqualTo(reward2);
    }
    
    @Test
    public void givenAgentInObsMapButNotInActionRewardMap_whenCombineObsActReward_thenAgentNotInResult() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);
        
        ObservationPositionsValues observation = new ObservationPositionsValues();
        observation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        
        Map<MLKAgent, Observation> observationMap = new HashMap<>();
        observationMap.put(agent, observation);
        
        Map<MLKAgent, Pair<Action, Reward>> actionRewardMap = new HashMap<>();
        // Deliberately not adding the agent to actionRewardMap
        
        // When
        Map<MLKAgent, Experience> result = environment.testCombineObsActReward(actionRewardMap, observationMap);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    public void givenTwoObservationMaps_whenMergeObservations_thenCorrectlyMerged() {
        // Given
        MLKAgent agent = mock(MLKAgent.class);
        
        ObservationPositionsValues observation1 = new ObservationPositionsValues();
        observation1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        
        ObservationPositionsValues observation2 = new ObservationPositionsValues();
        observation2.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 2.0));
        
        ObservationPositionsValues mergedObservation = new ObservationPositionsValues();
        mergedObservation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        mergedObservation.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 2.0));
        
        Map<MLKAgent, Observation> observationMap1 = new HashMap<>();
        observationMap1.put(agent, observation1);
        
        Map<MLKAgent, Observation> observationMap2 = new HashMap<>();
        observationMap2.put(agent, observation2);
        
        // When
        Map<MLKAgent, Observation> result = environment.testMergeObservations(observationMap1, observationMap2);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.containsKey(agent)).isTrue();
        
        Observation mergedResult = result.get(agent);
        assertThat(mergedResult).isInstanceOf(ObservationPositionsValues.class);
        
        ObservationPositionsValues typedResult = (ObservationPositionsValues) mergedResult;
        assertThat(typedResult.getListObs()).hasSize(2);
    }
    
    @Test
    public void givenAgentOnlyInFirstMap_whenMergeObservations_thenAgentInResultWithFirstObservation() {
        // Given
        MLKAgent agent1 = mock(MLKAgent.class);
        MLKAgent agent2 = mock(MLKAgent.class);
        
        ObservationPositionsValues observation1 = new ObservationPositionsValues();
        observation1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        
        ObservationPositionsValues observation2 = new ObservationPositionsValues();
        observation2.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 2.0));
        
        Map<MLKAgent, Observation> observationMap1 = new HashMap<>();
        observationMap1.put(agent1, observation1);
        
        Map<MLKAgent, Observation> observationMap2 = new HashMap<>();
        observationMap2.put(agent2, observation2);
        
        // When
        Map<MLKAgent, Observation> result = environment.testMergeObservations(observationMap1, observationMap2);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.containsKey(agent1)).isTrue();
        assertThat(result.containsKey(agent2)).isTrue();
        
        Observation resultObs1 = result.get(agent1);
        assertThat(resultObs1).isEqualTo(observation1);
    }
    
    @Test
    public void givenMultipleAgentsInBothMaps_whenMergeObservations_thenCorrectlyMerged() {
        // Given
        MLKAgent agent1 = mock(MLKAgent.class);
        MLKAgent agent2 = mock(MLKAgent.class);
        
        // Create observations for map 1
        ObservationPositionsValues observation1Agent1 = new ObservationPositionsValues();
        observation1Agent1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(1.0, 2.0)), 1.0));
        
        ObservationPositionsValues observation1Agent2 = new ObservationPositionsValues();
        observation1Agent2.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(3.0, 4.0)), 2.0));
        
        // Create observations for map 2
        ObservationPositionsValues observation2Agent1 = new ObservationPositionsValues();
        observation2Agent1.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(5.0, 6.0)), 3.0));
        
        ObservationPositionsValues observation2Agent2 = new ObservationPositionsValues();
        observation2Agent2.addObservation(new ObservationPositionValue(new Tuple(Arrays.asList(7.0, 8.0)), 4.0));
        
        Map<MLKAgent, Observation> observationMap1 = new HashMap<>();
        observationMap1.put(agent1, observation1Agent1);
        observationMap1.put(agent2, observation1Agent2);
        
        Map<MLKAgent, Observation> observationMap2 = new HashMap<>();
        observationMap2.put(agent1, observation2Agent1);
        observationMap2.put(agent2, observation2Agent2);
        
        // When
        Map<MLKAgent, Observation> result = environment.testMergeObservations(observationMap1, observationMap2);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.containsKey(agent1)).isTrue();
        assertThat(result.containsKey(agent2)).isTrue();
        
        // Both agents' observations should have been merged
        ObservationPositionsValues resultAgent1 = (ObservationPositionsValues) result.get(agent1);
        ObservationPositionsValues resultAgent2 = (ObservationPositionsValues) result.get(agent2);
        
        assertThat(resultAgent1.getListObs()).hasSize(2);
        assertThat(resultAgent2.getListObs()).hasSize(2);
    }
    
    /**
     * Test environment that exposes protected methods for testing
     */
    private static class TestEnvironment extends EnvironmentStandard {
        
        public TestEnvironment(int width, int height) {
            super(width, height);
        }
        
        @Override
        protected void setupState() {
            // Not needed for tests
        }
        
        @Override
        public Map<MLKAgent, Pair<Action, Reward>> dynamics(Map<MLKAgent, Action> actions) {
            // Not needed for tests
            return new HashMap<>();
        }
        
        @Override
        protected State getState() {
            // Not needed for tests
            return null;
        }
        
        // Expose protected methods for testing
        public Map<MLKAgent, Experience> testCombineObsActReward(
                Map<MLKAgent, Pair<Action, Reward>> actionRewardMap,
                Map<MLKAgent, Observation> observationMap) {
            return combineObsActReward(actionRewardMap, observationMap);
        }
        
        public Map<MLKAgent, Observation> testMergeObservations(
                Map<MLKAgent, Observation> observations1,
                Map<MLKAgent, Observation> observations2) {
            return mergeObservations(observations1, observations2);
        }

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setupAgent(MLKAgent agent) {
			// TODO Auto-generated method stub
			
		}
    }
}
