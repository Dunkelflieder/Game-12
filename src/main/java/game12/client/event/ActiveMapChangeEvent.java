package game12.client.event;

import de.nerogar.noise.event.Event;
import game12.client.map.ClientMap;

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
