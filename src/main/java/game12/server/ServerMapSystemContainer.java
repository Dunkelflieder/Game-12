package game12.server;

import game12.core.EntityFactorySystem;
import game12.core.MapSystemContainer;
import game12.core.Side;
import game12.server.map.ServerMap;
import game12.server.systems.EnemyPathingSystem;
import game12.server.systems.GameSetupSystem;
import game12.server.systems.SendEntityMoveSystem;
import game12.server.systems.SendEntitySystem;
import game12.server.systems.request.ComponentDebugRequestSystem;
import game12.server.systems.request.PlayerPositionUpdateRequestSystem;

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

		addSystem(new EnemyPathingSystem(getMap()));
		addSystem(new GameSetupSystem(getMap()));

		addSystem(new PlayerPositionUpdateRequestSystem(getMap()));

		// debug
		addSystem(new ComponentDebugRequestSystem(getMap()));
	}

	@Override
	public Side getSide() { return Side.SERVER; }

}
