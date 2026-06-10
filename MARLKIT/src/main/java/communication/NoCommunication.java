package communication;

import agent.MLKAgent;
import madkit.kernel.Mailbox;

public class NoCommunication implements CommunicationModel {

	@Override
	public void communicate(MLKAgent agent) {
		//Do Nothing
	}
	
	@Override
	public void handleCommunication(MLKAgent agent, Mailbox mailbox) {
		// Do Nothing
	}

}
