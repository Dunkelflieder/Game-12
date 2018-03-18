package game12.server.systems.behaviors;

import game12.core.EntityFactorySystem;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.systems.MapSystem;
import game12.server.components.SpawnEntitiesBehaviorComponent;
import game12.server.map.ServerMap;

import java.util.Random;

public class SpawnEntitiesBehaviorSystem extends BehaviorSystem<SpawnEntitiesBehaviorComponent> {

	private final MapSystem           mapSystem;
	private final EntityFactorySystem entityFactorySystem;

	private Random random;

	public SpawnEntitiesBehaviorSystem(ServerMap map) {
		super(SpawnEntitiesBehaviorComponent.class, map);

		mapSystem = map.getSystem(MapSystem.class);
		entityFactorySystem = map.getSystem(EntityFactorySystem.class);

		this.random = new Random();
	}

	@Override
	protected void behaviourFunction(UpdateEvent event, SpawnEntitiesBehaviorComponent behaviour) {
		behaviour.spawnTimer.update(event.getDelta());

		while (behaviour.spawnTimer.trigger()) {

			PositionComponent position = behaviour.getEntity().getComponent(PositionComponent.class);

			float spawnX = 0;
			float spawnY = 0;

			boolean spawnFound = false;

			while (!spawnFound) {
				spawnX = random.nextFloat() * behaviour.maxSpawnDistance * 2 + position.getX();
				spawnY = random.nextFloat() * behaviour.maxSpawnDistance * 2 + position.getZ();

				if (mapSystem.get((int) spawnX, (int) spawnY) == behaviour.getOwnRoom() && mapSystem.isWalkable((int) spawnX, (int) spawnY, false)) {
					spawnFound = true;
				}
			}

			entityFactorySystem.createEntity(behaviour.entityId, spawnX, 0, spawnY);
		}

	}

}
