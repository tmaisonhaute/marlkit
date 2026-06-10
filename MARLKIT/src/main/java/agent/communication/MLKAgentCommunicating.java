package agent.communication;

import agent.MLKAgent;
import communication.CommunicationModel;
import madkit.kernel.Mailbox;

public interface MLKAgentCommunicating extends MLKAgent {
	public static final String DEFAULT_AGENT_ROLE = "MLKCommunicatingAgents";
	
	Mailbox getCommunicationMailbox();
	
	/** 
	 * Returns the communication module used by this agent for interacting with other agents.
	*/
	public CommunicationModel getCommunicationModule();
	
	/**
	 * Sets the communication module for this agent, which will be used for interacting with other agents.
	 * @param communicationModule
	 */
	public void setCommunicationModule(CommunicationModel communicationModule);
	
	/**
	 * Communicates with other agents using the communication module.
	 */
	public default void communicate(){
		getCommunicationModule().communicate(this);
	}
	
	/**
	 * Handles communication system for this agent using its mailbox.
	 */
	public default void handleCommunication() {
		this.handleCommunication(getCommunicationMailbox());
	}
	
	
	public default void handleCommunication(Mailbox mailbox) {
		getCommunicationModule().handleCommunication(this, mailbox);
	}

	
}

