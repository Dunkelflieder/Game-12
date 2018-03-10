package game12.server.systems;

import game12.core.EntityFactorySystem;
import game12.core.LogicSystem;
import game12.core.event.StartGameEvent;
import game12.core.map.Entity;
import game12.core.systems.GameObjectsSystem;
import game12.server.components.JumpBehaviorComponent;
import game12.server.map.ServerMap;

public class GameSetupSystem extends LogicSystem {

	private final ServerMap map;

	public GameSetupSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		getEventManager().register(StartGameEvent.class, event -> start());
	}

	private void start() {
		EntityFactorySystem entityFactorySystem = getContainer().getSystem(EntityFactorySystem.class);

		short playerBlueprintId = map.getGameSystem(GameObjectsSystem.class).getID("player");
		entityFactorySystem.createEntity(playerBlueprintId, 1, 0, 1);

		short testEnemyBlueprintId = map.getGameSystem(GameObjectsSystem.class).getID("skeleton");
		Entity skeleton = entityFactorySystem.createEntity(testEnemyBlueprintId, 1, 0, 1);

		for (int i = 0; i < 10; i++) {
			short turretBlueprintId = map.getGameSystem(GameObjectsSystem.class).getID("turret");
			Entity turret = entityFactorySystem.createEntity(turretBlueprintId, 10 + i, 0, 10);
		}

		for (int i = 0; i < 5; i++) {
			short spiderBlueprintId = map.getGameSystem(GameObjectsSystem.class).getID("spider");
			Entity spider = entityFactorySystem.createEntity(spiderBlueprintId, 10, 0, 10);
			spider.getComponent(JumpBehaviorComponent.class).setOwnRoom(1);
		}
	}
}
