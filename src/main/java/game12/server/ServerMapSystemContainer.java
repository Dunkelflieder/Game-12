package game12.server;

import game12.core.EntityFactorySystem;
import game12.core.MapSystemContainer;
import game12.core.Side;
import game12.server.map.ServerMap;
import game12.server.systems.*;
import game12.server.systems.request.*;

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

		addSystem(new SetBehaviorRoomSystem(getMap()));
		addSystem(new EnemyPathingSystem(getMap()));

		addSystem(new JumpBehaviorSystem(getMap()));
		addSystem(new TurretBehaviorSystem(getMap()));
		addSystem(new SpawnEntitiesBehaviorSystem(getMap()));
		addSystem(new TurretBossBehaviorSystem(getMap()));

		addSystem(new GameSetupSystem(getMap()));
		addSystem(new ProjectileSystem(getMap()));
		addSystem(new HealthSystem(getMap()));
		addSystem(new DamageSystem(getMap()));

		addSystem(new MapChangeRequestSystem(getMap()));
		addSystem(new PlayerPositionUpdateRequestSystem());
		addSystem(new EntityPlaceRequestSystem(getMap()));
		addSystem(new ShootRequestSystem(getMap()));

		// debug
		addSystem(new ComponentDebugRequestSystem(getMap()));
	}

	@Override
	public Side getSide() { return Side.SERVER; }

}
