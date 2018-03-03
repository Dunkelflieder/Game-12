package strategy.client;

import strategy.client.map.ClientMap;
import strategy.core.Faction;
import strategy.core.FactionMapSystemContainer;
import strategy.core.Side;

public class ClientFactionMapSystemContainer extends FactionMapSystemContainer<ClientMap> {

	public ClientFactionMapSystemContainer(ClientMap map, Faction faction, int mapId) {
		super(map, faction, mapId);
	}

	@Override
	protected void addSystems() {
		super.addSystems();

	}

	@Override
	public Side getSide() { return Side.CLIENT; }

}
