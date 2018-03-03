package game12.server;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import game12.core.GameSystemContainer;
import game12.core.Side;

public class ServerGameSystemContainer extends GameSystemContainer<ServerMapSystemContainer> {

	public ServerGameSystemContainer(EventManager eventManager, INetworkAdapter networkAdapter, ServerMapSystemContainer[] mapSystemContainers) {
		super(eventManager, networkAdapter, mapSystemContainers);
	}

	@Override
	protected void addSystems() {
		super.addSystems();

		//addSystem(new NetworkEventTrigger());
	}

	@Override
	public Side getSide() { return Side.SERVER; }

}
