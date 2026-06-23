package communication;

import agent.MLKAgent;
import madkit.kernel.Mailbox;

public interface CommunicationModel {
	public default void communicatePreInfluence(MLKAgent agent) {}
	public default void handleCommunicationPreInfluence(MLKAgent agent, Mailbox mailbox) {}

	public default void communicatePostReaction(MLKAgent agent) {}
	public default void handleCommunicationPostReaction(MLKAgent agent, Mailbox mailbox) {}
	
	public default void communicateEndEpisode(MLKAgent agent) {}
	public default void handleCommunicationEndEpisode(MLKAgent agent, Mailbox mailbox) {}
}
