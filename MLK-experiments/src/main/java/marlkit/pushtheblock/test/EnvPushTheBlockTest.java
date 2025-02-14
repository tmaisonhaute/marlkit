//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import agent.MLKAgent;
//import agent.action.Action2DMove;
//import environment.reward.Reward;
//import environment.state.State2DGridInt;
//import marlkit.pushTheBlock.EnvPushTheBlock;
//import util.Pair;
//public class EnvPushTheBlockTest {
//
//    private EnvPushTheBlock env;
//    private MLKAgent agent;
//    private Action2DMove action;
//    private State2DGridInt state;
//
//    @BeforeEach
//    public void setUp() {	
//        env = new EnvPushTheBlock();
//        agent = mock(MLKAgent.class);
//        action = mock(Action2DMove.class);
//        state = mock(State2DGridInt.class);
//        env.state = state;
//        env.agents = mock(Agents.class);
//        when(env.agents.getAgents()).thenReturn(List.of(agent));
//    }
//
//    @Test
//    public void testDynamics_whenAgentMovesToBlock_thenBlockIsPushedAndRewardIsGiven() {
//        // Given
//        Map<MLKAgent, Action> actions = new HashMap<>();
//        actions.put(agent, action);
//        Pair<Integer, Integer> initialPosition = new Pair<>(2, 2);
//        Pair<Integer, Integer> moveValue = new Pair<>(1, 0);
//        Pair<Integer, Integer> newPosition = new Pair<>(3, 2);
//        when(state.getAgentPosition(agent)).thenReturn(initialPosition);
//        when(action.getValue()).thenReturn(moveValue);
//        when(state.getValue(newPosition)).thenReturn(1);
//
//        // When
//        Map<MLKAgent, Pair<Action, Reward>> results = env.dynamics(actions);
//
//        // Then
//        assertNotNull(results);
//        assertTrue(results.containsKey(agent));
//        Pair<Action, Reward> result = results.get(agent);
//        assertEquals(action, result.getFirst());
//        assertEquals(1, result.getSecond().getReward());
//        verify(state).moveAgent(agent, moveValue);
//        verify(state).setValue(newPosition, 0);
//    }
//}
package marlkit;


