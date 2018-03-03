package strategy.client;

import strategy.client.map.ClientMap;
import strategy.client.systems.*;
import strategy.core.EntityFactorySystem;
import strategy.core.MapSystemContainer;
import strategy.core.Side;

public class ClientMapSystemContainer extends MapSystemContainer<ClientMap> {

	public ClientMapSystemContainer(ClientMap map) {
		super(map);
	}

	@Override
	protected void addSystems() {
		super.addSystems();

		addSystem(new EntityFactorySystem(getMap(), false));
		addSystem(new RenderSystem(getMap()));

		// TODO remove network events and only use synchronized systems
		addSystem(new NetworkEventTrigger());

		addSystem(new UpdateRenderablesSystem(getMap()));
		addSystem(new UpdateLightsSystem(getMap()));
		addSystem(new ReceiveEntitySystem(getMap()));
		addSystem(new ReceiveEntityMoveSystem(getMap()));

		//addSystem(new SynchronizeComponentSystem(getMap()));

	}

	@Override
	public Side getSide() { return Side.CLIENT; }

}
