package game12.server;

import game12.core.Faction;
import game12.core.FactionMapSystemContainer;
import game12.core.Side;
import game12.server.map.ServerMap;
import game12.server.systems.RequestTriggerSystem;

public class ServerFactionMapSystemContainer extends FactionMapSystemContainer<ServerMap> {

	public ServerFactionMapSystemContainer(ServerMap map, Faction faction, int mapId) {
		super(map, faction, mapId);
	}

	@Override
	protected void addSystems() {
		super.addSystems();

		addSystem(new RequestTriggerSystem(getFaction()));
	}

	@Override
	public Side getSide() { return Side.SERVER; }

}
