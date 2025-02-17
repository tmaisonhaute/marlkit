package marlkit.test;


import java.util.Map;

import agent.MLKAgent;
import environment.observation.Observation;
import environment.state.State2DGridInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;



public class State2DGridIntTest {

    public static void main(String[] args) {
        testGetObservations();
    }

    public static void testGetObservations() {
        // Given
        State2DGridInt state = new State2DGridInt(5, 5, 2, true);
        MLKAgent agent = Mockito.mock(MLKAgent.class);
        state.addAgent(agent, 2, 2);
        state.setValue(1, 1, 5);
        state.setValue(3, 3, 10);
        state.setValue(2, 2, 0); // Agent's position

        // When
        Map<MLKAgent, Observation> observations = state.getObservations();

        // Then
        if (observations.containsKey(agent)) {
            Observation obs = observations.get(agent);
            System.out.println("testGetObservations passed: " + obs);
        } else {
            System.out.println("testGetObservations failed: No observation for agent");
        }
    }
}
