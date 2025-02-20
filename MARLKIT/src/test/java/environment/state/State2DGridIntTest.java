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
		
		//CHeck that the observations are not null
		assertThat(observations).isNotNull();
		assertThat(observations2).isNotNull();
		
		//check that the observations are equal
		Observation observation = observations.get(agent);
		assertThat(observation).isInstanceOf(ObservationPositionsValues.class);
		
		Observation observation2 = observations2.get(agent);
		assertThat(observation).isEqualTo(observation2);
		
		//check that the observation is correct
		if (observation instanceof ObservationPositionsValues opv) {
			Tuple expectedDistance = new Tuple(List.of(-1.0, -1.0));
			assertThat(opv.getObs(0).getPosition()).isEqualTo(expectedDistance);
		}
		else {
			assertThat(false).isTrue();
		}
	}
}

