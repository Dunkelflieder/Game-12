package game12.server.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import game12.core.EventTimer;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

public class SpawnEntitiesBehaviorComponent extends BehaviorComponent {

	public short entityId;

	private float      spawnDelay;
	public  EventTimer spawnTimer;

	public float maxSpawnDistance;

	public SpawnEntitiesBehaviorComponent() {
	}

	public SpawnEntitiesBehaviorComponent(short entityId, float spawnDelay, float maxSpawnDistance) {
		this.entityId = entityId;
		this.spawnDelay = spawnDelay;
		this.maxSpawnDistance = maxSpawnDistance;

		this.spawnTimer = new EventTimer(spawnDelay, true);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		entityId = gameObjectsSystem.getID(data.getStringUTF8("entity"));
		spawnDelay = data.getFloat("spawnDelay");
		maxSpawnDistance = data.getFloat("maxSpawnDistance");
	}

	@Override
	public Component clone() {
		return new SpawnEntitiesBehaviorComponent(entityId, spawnDelay, maxSpawnDistance);
	}
}
