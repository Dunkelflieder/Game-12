package game12.client.map;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import game12.core.Faction;
import game12.core.Side;
import game12.core.map.CoreMap;
import game12.core.network.FactionRequestPacket;

public class ClientMap extends CoreMap {

	private boolean isActive;

	public ClientMap(
			int id,
			INetworkAdapter networkAdapter,
			Faction[] factions
	                ) {
		super(id, networkAdapter, new EventManager("ClientMap" + id), factions);

	}

	@Override
	public Side getSide() { return Side.CLIENT; }

	public void makeRequest(FactionRequestPacket request) {
		getNetworkAdapter().send(request);
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean isActive() {
		return isActive;
	}

}
