package strategy.client.map;

import de.nerogar.noise.network.Connection;
import strategy.Strategy;
import strategy.core.Faction;
import strategy.core.map.MapLoader;

import java.util.List;

public class ClientMapLoader extends MapLoader<ClientMap> {

	private final Connection connection;

	public ClientMapLoader(List<ClientMap> maps, String mapID, Connection connection, Faction[] factions) {
		super(maps, mapID, factions);

		this.connection = connection;
	}

	@Override
	protected ClientMap newMap(int id, Faction[] factions) {
		return new ClientMap(id, connection.getNetworkAdapter(Strategy.NETWORK_ADAPTER_START_MAPS + id), factions);
	}

}
