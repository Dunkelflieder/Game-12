package game12.client.map;

import de.nerogar.noise.network.Connection;
import game12.Game12;
import game12.core.Faction;
import game12.core.map.MapLoader;

import java.util.List;

public class ClientMapLoader extends MapLoader<ClientMap> {

	private final Connection connection;

	public ClientMapLoader(List<ClientMap> maps, String mapID, Connection connection, Faction[] factions) {
		super(maps, mapID, factions);

		this.connection = connection;
	}

	@Override
	protected ClientMap newMap(int id, Faction[] factions) {
		return new ClientMap(id, connection.getNetworkAdapter(Game12.NETWORK_ADAPTER_START_MAPS + id), factions);
	}

}
