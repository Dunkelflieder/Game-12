package strategy.client;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import strategy.client.systems.EntityRenderResourcesSystem;
import strategy.core.GameSystemContainer;
import strategy.core.Side;

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
