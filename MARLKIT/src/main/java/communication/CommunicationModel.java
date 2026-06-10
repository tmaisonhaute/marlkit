package communication;

import agent.MLKAgent;
import madkit.kernel.Mailbox;

public interface CommunicationModel {
	public void communicate(MLKAgent agent);
	public void handleCommunication(MLKAgent agent, Mailbox mailbox);
}
