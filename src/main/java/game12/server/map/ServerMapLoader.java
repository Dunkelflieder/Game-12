package game12.server.map;

import de.nerogar.noise.network.ServerThread;
import game12.Game12;
import game12.core.Faction;
import game12.core.map.MapLoader;

import java.util.List;

public class ServerMapLoader extends MapLoader<ServerMap> {

	private final ServerThread serverThread;

	public ServerMapLoader(List<ServerMap> maps, String mapID, ServerThread serverThread, Faction[] factions) {
		super(maps, mapID, factions);

		this.serverThread = serverThread;
	}

	@Override
	protected ServerMap newMap(int id, Faction[] factions) {
		return new ServerMap(id, serverThread.getNetworkAdapter(Game12.NETWORK_ADAPTER_START_MAPS + id), factions);
	}

}
