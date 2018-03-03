package strategy.core.event;

import de.nerogar.noise.event.Event;
import strategy.core.map.Entity;

public class EntitySpawnEvent implements Event {

	private Entity entity;

	public EntitySpawnEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

}
