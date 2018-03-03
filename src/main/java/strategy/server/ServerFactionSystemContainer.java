package strategy.server;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import strategy.core.Faction;
import strategy.core.FactionSystemContainer;
import strategy.core.Side;

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
