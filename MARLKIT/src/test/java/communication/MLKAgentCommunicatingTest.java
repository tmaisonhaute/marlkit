package communication;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.action.Action;
import agent.communication.MLKAgentCommunicating;
import environment.MLKEnvironment;
import environment.observation.Observation;
import learning.Experience;
import learning.algorithm.Algorithm;
import learning.policy.Policy;
import learning.policy.PolicyInput;
import madkit.kernel.Mailbox;
import madkit.simulation.SimuAgent;

public class MLKAgentCommunicatingTest {

    @Test
    public void givenCommunicatingAgent_whenCommunicate_thenCommunicationModuleIsCalledWithAgent() {
        // Given
        RecordingCommunicationModule module = new RecordingCommunicationModule();
        DummyCommunicatingAgent agent = new DummyCommunicatingAgent(module);

        // When
        agent.communicate();

        // Then
        assertThat(module.communicateCalls).containsExactly(agent);
        assertThat(module.handleCommunicationCalls).isEmpty();
    }

    @Test
    public void givenCommunicatingAgent_whenHandleCommunicationWithoutArgument_thenUseAgentCommunicationMailbox() {
        // Given
        RecordingCommunicationModule module = new RecordingCommunicationModule();
        DummyCommunicatingAgent agent = new DummyCommunicatingAgent(module);

        // When
        agent.handleCommunication();

        // Then
        assertThat(module.handleCommunicationCalls)
                .containsExactly(new HandleCommunicationCall(agent, agent.getCommunicationMailbox()));
    }

    @Test
    public void givenCommunicatingAgent_whenHandleCommunicationWithMailbox_thenCommunicationModuleIsCalledWithProvidedMailbox() {
        // Given
        RecordingCommunicationModule module = new RecordingCommunicationModule();
        DummyCommunicatingAgent agent = new DummyCommunicatingAgent(module);
        Mailbox mailbox = null;

        // When
        agent.handleCommunication(mailbox);

        // Then
        assertThat(module.handleCommunicationCalls)
                .containsExactly(new HandleCommunicationCall(agent, mailbox));
    }

    @Test
    public void givenCommunicatingAgent_whenSetCommunicationModule_thenNewModuleIsUsed() {
        // Given
        RecordingCommunicationModule oldModule = new RecordingCommunicationModule();
        RecordingCommunicationModule newModule = new RecordingCommunicationModule();
        DummyCommunicatingAgent agent = new DummyCommunicatingAgent(oldModule);

        // When
        agent.setCommunicationModule(newModule);
        agent.communicate();

        // Then
        assertThat(oldModule.communicateCalls).isEmpty();
        assertThat(newModule.communicateCalls).containsExactly(agent);
    }

    @Test
    public void givenCommunicatingAgent_whenCallingCommunicateSeveralTimes_thenModuleReceivesAllCallsInOrder() {
        // Given
        RecordingCommunicationModule module = new RecordingCommunicationModule();
        DummyCommunicatingAgent agent = new DummyCommunicatingAgent(module);

        // When
        agent.communicate();
        agent.communicate();

        // Then
        assertThat(module.communicateCalls).containsExactly(agent, agent);
    }

    @Test
    public void givenCommunicatingAgent_whenCallingCommunicateAndHandleCommunication_thenBothPhasesAreSeparated() {
        // Given
        RecordingCommunicationModule module = new RecordingCommunicationModule();
        DummyCommunicatingAgent agent = new DummyCommunicatingAgent(module);

        // When
        agent.communicate();
        agent.handleCommunication();

        // Then
        assertThat(module.communicateCalls).containsExactly(agent);
        assertThat(module.handleCommunicationCalls)
                .containsExactly(new HandleCommunicationCall(agent, agent.getCommunicationMailbox()));
    }

    private static final class RecordingCommunicationModule implements CommunicationModule {

        private final List<MLKAgent> communicateCalls = new ArrayList<>();
        private final List<HandleCommunicationCall> handleCommunicationCalls = new ArrayList<>();

        @Override
        public void communicate(MLKAgent agent) {
            communicateCalls.add(agent);
        }

        @Override
        public void handleCommunication(MLKAgent agent, Mailbox mailbox) {
            handleCommunicationCalls.add(new HandleCommunicationCall(agent, mailbox));
        }
    }

    private record HandleCommunicationCall(MLKAgent agent, Mailbox mailbox) {
    }

    private static final class DummyCommunicatingAgent implements MLKAgentCommunicating {

        private CommunicationModule communicationModule;
        private final Mailbox communicationMailbox;

        private DummyCommunicatingAgent(CommunicationModule communicationModule) {
            this.communicationModule = communicationModule;
            this.communicationMailbox = null;
        }

        @Override
        public CommunicationModule getCommunicationModule() {
            return communicationModule;
        }

        @Override
        public void setCommunicationModule(CommunicationModule communicationModule) {
            this.communicationModule = communicationModule;
        }

        @Override
        public Mailbox getCommunicationMailbox() {
            return communicationMailbox;
        }

        @Override
        public Policy getPolicy() {
            return null;
        }

        @Override
        public Algorithm getAlgorithm() {
            return null;
        }

        @Override
        public Observation getRegisteredObservation() {
            return null;
        }

        @Override
        public void setRegisteredObservation(Observation registeredObservation) {
        }

        @Override
        public RandomGenerator prng() {
            return new java.util.Random(0);
        }

        @Override
        public void notifySelfToEnvironment() {
        }

        @Override
        public void setPolicy(Policy policy) {
        }

        @Override
        public void setAlgorithm(Algorithm algorithm) {
        }

        @Override
        public void initializeAll() {
        }

        @Override
        public void feedbackExperience(PolicyInput input, Action act, reward.Reward rew) {
        }

        @Override
        public void feedbackExperience(Experience experience) {
        }

        @Override
        public Action selectAction(PolicyInput input) {
            return null;
        }

        @Override
        public void takeAction() {
        }

        @Override
        public void collectExperience() {
        }

        @Override
        public Experience getEnvExperience() {
            return null;
        }

        @Override
        public MLKEnvironment getMLKEnvironment() {
            return null;
        }

        @Override
        public void updatePolicy(int timestep) {
        }

        @Override
        public void learnOnBatch() {
        }

        @Override
        public void endEpisode() {
        }

        @Override
        public SimuAgent getSimuAgent() {
            return null;
        }
    }
}