package communication;

import agent.MLKAgent;
import madkit.kernel.Mailbox;

public interface CommunicationModel {
	/**
	 * Called by an agent to communicate with other agents before the influence step.
	 * @param agent the agent that is communicating
	 */
	public default void communicatePreInfluence(MLKAgent agent) {}
	
	/**
	 * Called by an agent to handle communication from other agents before the influence step.
	 * @param agent the agent that is handling communication
	 * @param mailbox the mailbox containing messages from other agents
	 */
	public default void handleCommunicationPreInfluence(MLKAgent agent, Mailbox mailbox) {}

	/**
	 * Called by an agent to communicate with other agents after the influence step.
	 * @param agent the agent that is communicating
	 */
	public default void communicatePostReaction(MLKAgent agent) {}
	
	/**
	 * Called by an agent to handle communication from other agents after the influence step.
	 * @param agent the agent that is handling communication
	 * @param mailbox the mailbox containing messages from other agents
	 */
	public default void handleCommunicationPostReaction(MLKAgent agent, Mailbox mailbox) {}
	
	/**
	 * Called by an agent to communicate with other agents at the end of an episode.
	 * @param agent the agent that is communicating
	 */
	public default void communicateEndEpisode(MLKAgent agent) {}
	
	/**
	 * Called by an agent to handle communication from other agents at the end of an episode.
	 * @param agent the agent that is handling communication
	 * @param mailbox the mailbox containing messages from other agents
	 */
	public default void handleCommunicationEndEpisode(MLKAgent agent, Mailbox mailbox) {}
}


