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
import experience.Experience;
import learning.Algorithm;
import learning.Policy;
import madkit.kernel.Mailbox;
import madkit.simulation.SimuAgent;

public class MLKAgentCommunicatingTest {

    @Test
    public void givenCommunicatingAgent_whenCommunicate_thenCommunicationModuleIsCalledWithAgent() {
        // Given
        RecordingCommunicationModule module = new RecordingCommunicationModule();
        DummyCommunicatingAgent agent = new DummyCommunicatingAgent(module);

        // When
        agent.communicatePreInfluence();

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
        agent.handleCommunicationPreInfluence();

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
        agent.setCommunicationModel(newModule);
        agent.communicatePreInfluence();

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
        agent.communicatePreInfluence();
        agent.communicatePreInfluence();

        // Then
        assertThat(module.communicateCalls).containsExactly(agent, agent);
    }

    @Test
    public void givenCommunicatingAgent_whenCallingCommunicateAndHandleCommunication_thenBothPhasesAreSeparated() {
        // Given
        RecordingCommunicationModule module = new RecordingCommunicationModule();
        DummyCommunicatingAgent agent = new DummyCommunicatingAgent(module);

        // When
        agent.communicatePreInfluence();
        agent.handleCommunicationPreInfluence();

        // Then
        assertThat(module.communicateCalls).containsExactly(agent);
        assertThat(module.handleCommunicationCalls)
                .containsExactly(new HandleCommunicationCall(agent, agent.getCommunicationMailbox()));
    }

    private static final class RecordingCommunicationModule implements CommunicationModel {

        private final List<MLKAgent> communicateCalls = new ArrayList<>();
        private final List<HandleCommunicationCall> handleCommunicationCalls = new ArrayList<>();

        @Override
        public void communicatePreInfluence(MLKAgent agent) {
            communicateCalls.add(agent);
        }

        @Override
        public void handleCommunicationPreInfluence(MLKAgent agent, Mailbox mailbox) {
            handleCommunicationCalls.add(new HandleCommunicationCall(agent, mailbox));
        }
        
		public void communicatePostReaction(MLKAgent agent) {
			// Not needed for this test
		}
		
		public void handleCommunicationPostReaction(MLKAgent agent, Mailbox mailbox) {
			// Not needed for this test
		}
    }

    private record HandleCommunicationCall(MLKAgent agent, Mailbox mailbox) {
    }

    private static final class DummyCommunicatingAgent implements MLKAgentCommunicating {

        private CommunicationModel communicationModule;
        private final Mailbox communicationMailbox;

        private DummyCommunicatingAgent(CommunicationModel communicationModule) {
            this.communicationModule = communicationModule;
            this.communicationMailbox = null;
        }

        @Override
        public CommunicationModel getCommunicationModel() {
            return communicationModule;
        }

        @Override
        public void setCommunicationModel(CommunicationModel communicationModule) {
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
        public void feedbackExperience(Experience experience) {
        }

        @Override
        public Action selectAction(Observation input) {
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