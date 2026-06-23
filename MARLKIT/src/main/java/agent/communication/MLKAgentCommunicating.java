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
	 * This method is called before the influence phase of the environment, allowing the agent to send messages or information to other agents.
	 */
	public default void communicatePreInfluence(){
		getCommunicationModel().communicatePreInfluence(this);
	}
	
	/**
	 * Handles communication system for this agent before the influence phase of the environment using its mailbox.
	 */
	public default void handleCommunicationPreInfluence() {
		this.handleCommunication(getCommunicationMailbox());
	}
	
	/**
	 * Handles communication system for this agent using the specified mailbox.
	 * @param mailbox the mailbox composed of messages received from other agents that this agent needs to process.
	 */
	public default void handleCommunication(Mailbox mailbox) {
		getCommunicationModel().handleCommunicationPreInfluence(this, mailbox);
	}
	
	/**
	 * Communicates with other agents after the reaction phase of the environment using the communication model.
	 */
	public default void communicatePostReaction() {
		getCommunicationModel().communicatePostReaction(this);
	}
	
	/**
	 * Handles communication system for this agent after the reaction phase of the environment using its mailbox.
	 */
	public default void handleCommunicationPostReaction() {
		this.handleCommunicationPostReaction(getCommunicationMailbox());
	}
	
	/**
	 * Handles communication system for this agent after the reaction phase of the environment using the specified mailbox.
	 * @param mailbox the mailbox composed of messages received from other agents that this agent needs to process.
	 */
	public default void handleCommunicationPostReaction(Mailbox mailbox) {
		getCommunicationModel().handleCommunicationPostReaction(this, mailbox);
	}
	
	/**
	 * Communicates with other agents at the end of the episode using the communication model.
	 */
	public default void communicateEndEpisode() {
		getCommunicationModel().communicateEndEpisode(this);
	}
	
	/**
	 * Handles communication system for this agent at the end of the episode using its mailbox.
	 */
	public default void handleCommunicationEndEpisode() {
		this.handleCommunicationEndEpisode(getCommunicationMailbox());
	}
	
	/**
	 * Handles communication system for this agent at the end of the episode using the specified mailbox.
	 * @param mailbox the mailbox composed of messages received from other agents that this agent needs to process.
	 */
	public default void handleCommunicationEndEpisode(Mailbox mailbox) {
		getCommunicationModel().handleCommunicationEndEpisode(this, mailbox);
	}

	
}

