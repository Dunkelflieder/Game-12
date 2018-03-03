package strategy.client.event;

import de.nerogar.noise.event.Event;
import strategy.client.map.ClientMap;

public class ActiveMapChangeEvent implements Event {

	private ClientMap oldMap;
	private ClientMap newMap;

	public ActiveMapChangeEvent(ClientMap oldMap, ClientMap newMap) {
		this.oldMap = oldMap;
		this.newMap = newMap;
	}

	public ClientMap getOldMap() {
		return oldMap;
	}

	public ClientMap getNewMap() {
		return newMap;
	}

}
