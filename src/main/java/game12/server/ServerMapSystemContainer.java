package game12.server;

import game12.core.EntityFactorySystem;
import game12.core.MapSystemContainer;
import game12.core.Side;
import game12.server.map.ServerMap;
import game12.server.systems.*;
import game12.server.systems.request.ComponentDebugRequestSystem;
import game12.server.systems.request.MapChangeRequestSystem;
import game12.server.systems.request.PlayerPositionUpdateRequestSystem;
import game12.server.systems.request.ShootRequestSystem;

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
		addSystem(new ProjectileSystem(getMap()));

		addSystem(new MapChangeRequestSystem(getMap()));
		addSystem(new PlayerPositionUpdateRequestSystem(getMap()));
		addSystem(new ShootRequestSystem(getMap()));

		// debug
		addSystem(new ComponentDebugRequestSystem(getMap()));
	}

	@Override
	public Side getSide() { return Side.SERVER; }

}
