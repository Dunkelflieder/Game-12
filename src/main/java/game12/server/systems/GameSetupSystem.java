package game12.server.systems;

import game12.core.EntityFactorySystem;
import game12.core.LogicSystem;
import game12.core.event.StartGameEvent;
import game12.core.systems.GameObjectsSystem;
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

		short testEnemyBlueprintId = map.getGameSystem(GameObjectsSystem.class).getID("skeleton");
		entityFactorySystem.createEntity(testEnemyBlueprintId, 1, 0, 1);

		short playerBlueprintId = map.getGameSystem(GameObjectsSystem.class).getID("player");
		entityFactorySystem.createEntity(playerBlueprintId, 1, 0, 1);
	}
}
