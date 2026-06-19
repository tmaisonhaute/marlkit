package communication;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import agent.MLKAgent;
import agent.communication.AgentStandardCommunicating;
import learning.Algorithm;
import learning.Policy;
import madkit.kernel.Mailbox;

public class AgentStandardCommunicatingTest {

    @Test
    public void givenAgentStandardCommunicating_whenCreated_thenCommunicationModuleIsStored() {
        // Given
        Policy policy = null;
        Algorithm algorithm = null;
        RecordingCommunicationModule module = new RecordingCommunicationModule();

        // When
        AgentStandardCommunicating agent =
                new AgentStandardCommunicating(policy, algorithm, module);

        // Then
        assertThat(agent.getCommunicationModel()).isSameAs(module);
    }

    @Test
    public void givenAgentStandardCommunicating_whenSetCommunicationModule_thenModuleIsUpdated() {
        // Given
        AgentStandardCommunicating agent =
                new AgentStandardCommunicating(null, null, new RecordingCommunicationModule());

        RecordingCommunicationModule newModule = new RecordingCommunicationModule();

        // When
        agent.setCommunicationModel(newModule);

        // Then
        assertThat(agent.getCommunicationModel()).isSameAs(newModule);
    }

    @Test
    public void givenAgentStandardCommunicating_whenCommunicate_thenStoredModuleIsUsed() {
        // Given
        RecordingCommunicationModule module = new RecordingCommunicationModule();
        AgentStandardCommunicating agent =
                new AgentStandardCommunicating(null, null, module);

        // When
        agent.communicate();

        // Then
        assertThat(module.lastCommunicatingAgent).isSameAs(agent);
    }

    private static final class RecordingCommunicationModule implements CommunicationModel {

        private MLKAgent lastCommunicatingAgent;
        private MLKAgent lastHandlingAgent;
        private Mailbox lastMailbox;

        @Override
        public void communicate(MLKAgent agent) {
            this.lastCommunicatingAgent = agent;
        }

        @Override
        public void handleCommunication(MLKAgent agent, Mailbox mailbox) {
            this.lastHandlingAgent = agent;
            this.lastMailbox = mailbox;
        }
    }
}
