package strategy.client.event;

import de.nerogar.noise.event.Event;
import strategy.client.Client;

public class ClientChangeEvent implements Event {

	public static final int DISCONNECT = 0;
	public static final int CONNECT    = 1;

	private Client newClient;
	private int    action;

	public ClientChangeEvent(Client newClient) {
		this.newClient = newClient;

		this.action = newClient == null ? DISCONNECT : CONNECT;
	}

	public Client getNewClient() {
		return newClient;
	}

	public int getAction() {
		return action;
	}

}
