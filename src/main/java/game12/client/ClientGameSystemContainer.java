package game12.client;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import game12.client.systems.EntityRenderResourcesSystem;
import game12.core.GameSystemContainer;
import game12.core.Side;

public class ClientGameSystemContainer extends GameSystemContainer<ClientMapSystemContainer> {

	public ClientGameSystemContainer(EventManager eventManager, INetworkAdapter networkAdapter, ClientMapSystemContainer[] mapSystemContainers) {
		super(eventManager, networkAdapter, mapSystemContainers);

	}

	@Override
	protected void addSystems() {
		super.addSystems();

		addSystem(new EntityRenderResourcesSystem());
	}

	@Override
	public Side getSide() { return Side.CLIENT; }

}
