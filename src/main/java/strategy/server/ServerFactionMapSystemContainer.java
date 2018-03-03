package strategy.server;

import strategy.core.Faction;
import strategy.core.FactionMapSystemContainer;
import strategy.core.Side;
import strategy.server.map.ServerMap;
import strategy.server.systems.RequestTriggerSystem;

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
