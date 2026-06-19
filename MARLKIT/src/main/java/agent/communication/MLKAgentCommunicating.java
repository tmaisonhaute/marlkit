package agent.communication;

import agent.MLKAgent;
import communication.CommunicationModel;
import madkit.kernel.Mailbox;

public interface MLKAgentCommunicating extends MLKAgent {
	public static final String DEFAULT_AGENT_ROLE = "MLKCommunicatingAgents";
	
	Mailbox getCommunicationMailbox();
	
	/** 
	 * Returns the communication model used by this agent for interacting with other agents.
	*/
	public CommunicationModel getCommunicationModel();
	
	/**
	 * Sets the communication model for this agent, which will be used for interacting with other agents.
	 * @param communicationModel
	 */
	public void setCommunicationModel(CommunicationModel communicationModel);
	
	/**
	 * Communicates with other agents using the communication model.
	 */
	public default void communicate(){
		getCommunicationModel().communicate(this);
	}
	
	/**
	 * Handles communication system for this agent using its mailbox.
	 */
	public default void handleCommunication() {
		this.handleCommunication(getCommunicationMailbox());
	}
	
	
	public default void handleCommunication(Mailbox mailbox) {
		getCommunicationModel().handleCommunication(this, mailbox);
	}

	
}

