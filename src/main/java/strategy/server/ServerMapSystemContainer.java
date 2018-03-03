package strategy.server;

import strategy.core.EntityFactorySystem;
import strategy.core.MapSystemContainer;
import strategy.core.Side;
import strategy.server.map.ServerMap;
import strategy.server.systems.SendEntityMoveSystem;
import strategy.server.systems.SendEntitySystem;
import strategy.server.systems.request.ComponentDebugRequestSystem;

public class ServerMapSystemContainer extends MapSystemContainer<ServerMap> {

	public ServerMapSystemContainer(ServerMap map) {
		super(map);
	}

	@Override
	protected void addSystems() {
		super.addSystems();

		addSystem(new EntityFactorySystem(getMap(), false));

		addSystem(new SendEntitySystem());
		addSystem(new SendEntityMoveSystem());

		// debug
		addSystem(new ComponentDebugRequestSystem(getMap()));
	}

	@Override
	public Side getSide() { return Side.SERVER; }

}
