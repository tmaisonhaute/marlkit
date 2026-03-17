package agent.communication;

import agent.MLKAgent;
import madkit.kernel.Mailbox;

public interface MLKAgentCommunicating extends MLKAgent {
	public static final String DEFAULT_AGENT_ROLE = "MLKCommunicatingAgents";
	
	@Override
	public default void communicate(){
		getCommunicationModule().communicate(this);
	}
	
	@Override
	public default void handleCommunication(Mailbox mailbox) {
		getCommunicationModule().handleCommunication(this, mailbox);
	}
	
	@Override
	public default String getRole() {
		return DEFAULT_AGENT_ROLE;
	}
}

