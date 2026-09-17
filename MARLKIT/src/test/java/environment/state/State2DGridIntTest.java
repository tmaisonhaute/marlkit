package environment.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import agent.MLKAgent;
import environment.observation.Observation;
import environment.observation.ObservationPositionsValues;
import util.Tuple;

public class State2DGridIntTest {

	@Test
	public void givenGridWithAgent_whenGetObservations_thenReturnCorrectObservations() {
		// Given: a grid with an agent
		State2DGridInt grid = new State2DGridInt(3, 3);
		MLKAgent agent = mock(MLKAgent.class);
		grid.addAgent(agent, 1, 1);
		grid.setValue(0, 0, 1);

		// When: getObservations is called
		Map<MLKAgent, Observation> observations = grid.getObservations();
		Map<MLKAgent, Observation> observations2 = grid.getObservations();
		System.out.println(observations);

		// Then: the observations should be correct
		
		// Check that the observations are not null
		assertThat(observations).isNotNull();
		assertThat(observations2).isNotNull();
		
		// Check that the observations are equal
		Observation observation = observations.get(agent);
		assertThat(observation).isInstanceOf(ObservationPositionsValues.class);
		
		Observation observation2 = observations2.get(agent);
		assertThat(observation).isEqualTo(observation2);
		
		// Check that the observation is correct
		if (observation instanceof ObservationPositionsValues opv) {
			Tuple expectedDistance = new Tuple(List.of(-1.0, -1.0));
			assertThat(opv.getObs(0).getPosition()).isEqualTo(expectedDistance);
		}
		else {
			assertThat(false).isTrue();
		}
	}

	@Test
	public void givenGridWithMultipleAgents_whenGetAgentsInViewRange_thenReturnAgentsInRange() {
		// Given: a grid with multiple agents
		State2DGridInt grid = new State2DGridInt(5, 5, 2, true, true); // 5x5 grid with view range 2 and agent positions observation
		
		MLKAgent observingAgent = mock(MLKAgent.class);
		MLKAgent agentInRange1 = mock(MLKAgent.class);
		MLKAgent agentInRange2 = mock(MLKAgent.class);
		MLKAgent agentOutOfRange = mock(MLKAgent.class);
		
		// Place agents on the grid
		grid.addAgent(observingAgent, 2, 2);    // Center
		grid.addAgent(agentInRange1, 1, 1);     // Top-left (in range)
		grid.addAgent(agentInRange2, 3, 2);     // Right (in range)
		grid.addAgent(agentOutOfRange, 4, 4);   // Bottom-right (out of range with view range 2)
		
		// Create the list of visible cells for the observing agent
		List<Cell> visibleCells = grid.getNeighbors(observingAgent);
		
		// When: getAgentsInViewRange is called
		List<MLKAgent> agentsInRange = grid.getAgentsInViewRange(observingAgent, visibleCells);
		
		// Then: only agents within range should be returned
		assertThat(agentsInRange).hasSize(2);
		assertThat(agentsInRange).contains(agentInRange1, agentInRange2);
		assertThat(agentsInRange).doesNotContain(observingAgent); // Observing agent should not be included
		assertThat(agentsInRange).doesNotContain(agentOutOfRange); // Out of range agent should not be included
	}
	
	@Test
	public void givenGridWithAgentsAndAgentPositionsEnabled_whenGetObservations_thenIncludeAgentPositions() {
		// Given: a grid with multiple agents and observeAgentsPositions=true
		State2DGridInt grid = new State2DGridInt(5, 5, 2, true, true);
		
		MLKAgent observingAgent = mock(MLKAgent.class);
		MLKAgent otherAgent = mock(MLKAgent.class);
		
		grid.addAgent(observingAgent, 2, 2);
		grid.addAgent(otherAgent, 3, 2); // 1 cell to the right
		
		// When: get observations
		Map<MLKAgent, Observation> observations = grid.getObservations();
		
		// Then: observation should include both cell values and agent positions
		Observation observation = observations.get(observingAgent);
		assertThat(observation).isInstanceOf(ObservationPositionsValues.class);
		
		ObservationPositionsValues opv = (ObservationPositionsValues) observation;
		
		// Check if observation contains the observing agent's position (marked with -1)
		boolean containsObservingAgent = false;
		// Check if observation contains the other agent's position (marked with -2)
		boolean containsOtherAgent = false;
		
		for (int i = 0; i < opv.getListObs().size(); i++) {
			Tuple position = opv.getObs(i).getPosition();
			double value = opv.getObs(i).getValue();
			
			// Check for current agent (-1)
			if (value == -1.0 && position.equals(new Tuple(List.of(2.0, 2.0)))) {
				containsObservingAgent = true;
			}
			
			// Check for other agent (-2)
			if (value == -2.0 && position.equals(new Tuple(List.of(1.0, 0.0)))) { // Relative position (3-2, 2-2)
				containsOtherAgent = true;
			}
		}
		
		assertThat(containsObservingAgent).isTrue();
		assertThat(containsOtherAgent).isTrue();
	}
	
	@Test
	public void givenGridWithAgentsAndAgentPositionsDisabled_whenGetObservations_thenExcludeAgentPositions() {
		// Given: a grid with multiple agents and observeAgentsPositions=false
		State2DGridInt grid = new State2DGridInt(5, 5, 2, true, false);
		
		MLKAgent observingAgent = mock(MLKAgent.class);
		MLKAgent otherAgent = mock(MLKAgent.class);
		
		grid.addAgent(observingAgent, 2, 2);
		grid.addAgent(otherAgent, 3, 2);
		
		// Set a cell value
		grid.setValue(1, 1, 5);
		
		// When: get observations
		Map<MLKAgent, Observation> observations = grid.getObservations();
		
		// Then: observation should only include cell values, not agent positions
		Observation observation = observations.get(observingAgent);
		assertThat(observation).isInstanceOf(ObservationPositionsValues.class);
		
		ObservationPositionsValues opv = (ObservationPositionsValues) observation;
		
		// Check for any observations with value -1 or -2 (which would represent agents)
		boolean containsAgentMarkers = false;
		boolean containsCellValue = false;
		
		for (int i = 0; i < opv.getListObs().size(); i++) {
			double value = opv.getObs(i).getValue();
			
			if (value == -1.0 || value == -2.0) {
				containsAgentMarkers = true;
			}
			
			if (value == 5.0) {
				containsCellValue = true;
			}
		}
		
		assertThat(containsAgentMarkers).isFalse();
		assertThat(containsCellValue).isTrue();
	}
	
	@Test
	public void givenTwoMutuallyVisibleAgents_whenGetObservations_thenObservationsAreCorrectAndConsistent() {
	    // Given
	    State2DGridInt grid = new State2DGridInt(5, 5, 3, true, true);

	    MLKAgent firstAgent = mock(MLKAgent.class);
	    MLKAgent secondAgent = mock(MLKAgent.class);

	    grid.addAgent(firstAgent, 1, 1);
	    grid.addAgent(secondAgent, 3, 2);
	    grid.setValue(2, 2, 5);

	    // When
	    Map<MLKAgent, Observation> observations = grid.getObservations();

	    // Then
	    assertThat(observations).containsKeys(firstAgent, secondAgent);
	    assertThat(observations.get(firstAgent)).isInstanceOf(ObservationPositionsValues.class);
	    assertThat(observations.get(secondAgent)).isInstanceOf(ObservationPositionsValues.class);

	    ObservationPositionsValues firstObservation = (ObservationPositionsValues) observations.get(firstAgent);
	    ObservationPositionsValues secondObservation = (ObservationPositionsValues) observations.get(secondAgent);

	    Tuple firstAgentPosition = findPosition(firstObservation, -1.0);
	    Tuple secondAgentPosition = findPosition(secondObservation, -1.0);

	    Tuple secondRelativeToFirst = findPosition(firstObservation, -2.0);
	    Tuple firstRelativeToSecond = findPosition(secondObservation, -2.0);

	    Tuple resourceRelativeToFirst = findPosition(firstObservation, 5.0);
	    Tuple resourceRelativeToSecond = findPosition(secondObservation, 5.0);

	    assertThat(firstAgentPosition).isEqualTo(new Tuple(List.of(1.0, 1.0)));
	    assertThat(secondAgentPosition).isEqualTo(new Tuple(List.of(3.0, 2.0)));

	    assertThat(secondRelativeToFirst).isEqualTo(new Tuple(List.of(2.0, 1.0)));
	    assertThat(firstRelativeToSecond).isEqualTo(new Tuple(List.of(-2.0, -1.0)));

	    assertThat(resourceRelativeToFirst).isEqualTo(new Tuple(List.of(1.0, 1.0)));
	    assertThat(resourceRelativeToSecond).isEqualTo(new Tuple(List.of(-1.0, 0.0)));

	    assertThat(secondRelativeToFirst.getValue(0)).isEqualTo(-firstRelativeToSecond.getValue(0));
	    assertThat(secondRelativeToFirst.getValue(1)).isEqualTo(-firstRelativeToSecond.getValue(1));

	    assertThat(resourceRelativeToFirst.getValue(0) - secondRelativeToFirst.getValue(0)).isEqualTo(resourceRelativeToSecond.getValue(0));
	    assertThat(resourceRelativeToFirst.getValue(1) - secondRelativeToFirst.getValue(1)).isEqualTo(resourceRelativeToSecond.getValue(1));
	}
	
	/**
	 * Helper method to find the position of a specific value in the observation.
	 * @param observation the observation to search
	 * @param value the value to find
	 * @return the position of the value in the observation
	 */
	private Tuple findPosition(ObservationPositionsValues observation, double value) {
	    for (int i = 0; i < observation.getListObs().size(); i++) {
	        if (Double.compare(observation.getObs(i).getValue(), value) == 0) {
	            return observation.getObs(i).getPosition();
	        }
	    }

	    throw new AssertionError("No observation found with value " + value + ".");
	}
	
}

