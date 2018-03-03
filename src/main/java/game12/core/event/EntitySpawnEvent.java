package game12.core.event;

import de.nerogar.noise.event.Event;
import game12.core.map.Entity;

public class EntitySpawnEvent implements Event {

	private Entity entity;

	public EntitySpawnEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

}
