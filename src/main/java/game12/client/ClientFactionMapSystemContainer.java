package game12.client;

import game12.client.map.ClientMap;
import game12.core.Faction;
import game12.core.FactionMapSystemContainer;
import game12.core.Side;

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
