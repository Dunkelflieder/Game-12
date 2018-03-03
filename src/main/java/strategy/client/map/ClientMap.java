package strategy.client.map;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import strategy.core.Faction;
import strategy.core.Side;
import strategy.core.map.CoreMap;
import strategy.core.network.FactionRequestPacket;

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
