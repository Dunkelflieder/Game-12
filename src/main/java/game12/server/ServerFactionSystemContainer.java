package game12.server;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import game12.core.Faction;
import game12.core.FactionSystemContainer;
import game12.core.Side;

public class ServerFactionSystemContainer extends FactionSystemContainer {

	public ServerFactionSystemContainer(EventManager eventManager, INetworkAdapter networkAdapter, Faction faction) {
		super(eventManager, networkAdapter, faction);
	}

	@Override
	protected void addSystems() {
		super.addSystems();
	}

	@Override
	public Side getSide() { return Side.SERVER; }

}
