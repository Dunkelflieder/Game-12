package game12.client;

import game12.client.map.ClientMap;
import game12.client.systems.*;
import game12.core.EntityFactorySystem;
import game12.core.MapSystemContainer;
import game12.core.Side;

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
		addSystem(new ParticleSystem(getMap()));

		addSystem(new MapRenderSystem());

		//addSystem(new SynchronizeComponentSystem(getMap()));

	}

	@Override
	public Side getSide() { return Side.CLIENT; }

}
