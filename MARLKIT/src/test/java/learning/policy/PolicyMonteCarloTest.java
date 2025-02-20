package learning.policy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import agent.action.Action;
import agent.action.Action2DMove;
import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import learning.Experience;
import util.Pair;
import util.Tuple;

public class PolicyMonteCarloTest {

    @Test
    public void createStateActionEqual() {
    	//Given 
    	Tuple position1 = new Tuple(List.of(0.0, 0.0));
    	Tuple position2 = new Tuple(List.of(0.0, 0.0));
    	ObservationPositionValue obs1 = new ObservationPositionValue(position1, 1);
    	ObservationPositionValue obs2 = new ObservationPositionValue(position2, 1);
    	Action2DMove action1 = Action2DMove.up();
    	Action2DMove action2 = Action2DMove.up();
    	Reward reward1 = new RewardStandard(1);
    	Reward reward2 = new RewardStandard(0);
    	Experience experience1 = new Experience(obs1, action1, reward1);
    	Experience experience2 = new Experience(obs2, action2, reward2);
    	
    	PolicyMonteCarlo policy = new PolicyMonteCarlo(List.of(Action2DMove.up()));
    	
    	//When 
    	Pair<Observation, Action> stateAction1 = policy.createStateAction(experience1);
    	Pair<Observation, Action> stateAction2 = policy.createStateAction(experience2);
    	
    	//Then
    	assertThat(stateAction1).isEqualTo(stateAction2);
    	
    	
	}
	
	@Test
	public void givenExperiencesAndCumulativeRewards_whenUpdateQ_thenQValuesUpdatedCorrectly() {
	    // Given
	    Tuple position1 = new Tuple(List.of(0.0, 0.0));
	    Tuple position2 = new Tuple(List.of(1.0, 1.0));
	    ObservationPositionValue obs1 = new ObservationPositionValue(position1, 1);
	    ObservationPositionValue obs2 = new ObservationPositionValue(position2, 1);
	    Action2DMove action1 = Action2DMove.up();
	    Action2DMove action2 = Action2DMove.down();
	    Reward reward1 = new RewardStandard(1);
	    Reward reward2 = new RewardStandard(2);
	    Experience experience1 = new Experience(obs1, action1, reward1);
	    Experience experience2 = new Experience(obs2, action2, reward2);
	    List<Experience> experiences = List.of(experience1, experience2);
	    double[] cumulativeRewards = {1.0, 2.0};
	
	    PolicyMonteCarlo policy = new PolicyMonteCarlo(List.of(Action2DMove.up(), Action2DMove.down()));
	
	    // When
	    policy.updateQ(experiences, cumulativeRewards);
	
	    // Then
	    Pair<Observation, Action> stateAction1 = new Pair<>(obs1, action1);
	    Pair<Observation, Action> stateAction2 = new Pair<>(obs2, action2);
	    assertThat(policy.getQ()).containsEntry(stateAction1, 1.0);
	    assertThat(policy.getQ()).containsEntry(stateAction2, 2.0);
	}

	
	@Test
	public void givenEqualStateActions_whenUpdateQ_thenQSizeIsOne() {
	    // Given
	    Tuple position1 = new Tuple(List.of(0.0, 0.0));
	    Tuple position2 = new Tuple(List.of(0.0, 0.0));
	    ObservationPositionValue obs1 = new ObservationPositionValue(position1, 1);
	    ObservationPositionValue obs2 = new ObservationPositionValue(position2, 1);
	    Action2DMove action1 = Action2DMove.up();
	    Action2DMove action2 = Action2DMove.up();
	    Reward reward1 = new RewardStandard(1);
	    Reward reward2 = new RewardStandard(2);
	    Experience experience1 = new Experience(obs1, action1, reward1);
	    Experience experience2 = new Experience(obs2, action2, reward2);
	    List<Experience> experiences = List.of(experience1, experience2);
	    double[] cumulativeRewards = {1.0, 2.0};
	
	    PolicyMonteCarlo policy = new PolicyMonteCarlo(List.of(Action2DMove.up()));
	
	    // When
	    policy.updateQ(experiences, cumulativeRewards);
	
	    // Then
	    assertThat(policy.getQ()).hasSize(1);
	    Pair<Observation, Action> stateAction = new Pair<>(obs1, action1);
	    assertThat(policy.getQ()).containsEntry(stateAction, 1.5);
	}

	@Test
	public void givenEqualStateActions_whenPutInQ_thenQSizeIsOne() {
		// Given
		Tuple position1 = new Tuple(List.of(0.0, 0.0));
		Tuple position2 = new Tuple(List.of(0.0, 0.0));
		ObservationPositionValue obs1 = new ObservationPositionValue(position1, 1);
		ObservationPositionValue obs2 = new ObservationPositionValue(position2, 1);
		Action2DMove action1 = Action2DMove.up();
		Action2DMove action2 = Action2DMove.up();
		Pair<Observation, Action> stateAction1 = new Pair<>(obs1, action1);
		Pair<Observation, Action> stateAction2 = new Pair<>(obs2, action2);

		PolicyMonteCarlo policy = new PolicyMonteCarlo(List.of(Action2DMove.up()));

		// When
		Map<Pair<Observation, Action>, Double> qMap = policy.getQ();
		qMap.put(stateAction1, 1.0);
		qMap.put(stateAction2, 2.0);

		// Then
		assertThat(qMap).hasSize(1);
		assertThat(qMap).containsEntry(stateAction1, 2.0);
	}
	
}
