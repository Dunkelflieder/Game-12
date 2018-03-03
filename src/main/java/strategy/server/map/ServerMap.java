package strategy.server.map;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import strategy.core.Faction;
import strategy.core.Side;
import strategy.core.map.CoreMap;

public class ServerMap extends CoreMap {

	public ServerMap(
			int id,
			INetworkAdapter networkAdapter,
			Faction[] factions
	                ) {
		super(id, networkAdapter, new EventManager("ServerMap" + id), factions);
	}

	@Override
	public Side getSide() { return Side.SERVER; }

}
