package strategy.core.event;

import de.nerogar.noise.event.Event;
import strategy.core.map.Component;
import strategy.core.map.Entity;

import java.util.Map;

public class EntityDespawnEvent implements Event {

	private Entity                                     entity;
	private Map<Class<? extends Component>, Component> components;

	public EntityDespawnEvent(Entity entity, Map<Class<? extends Component>, Component> components) {
		this.entity = entity;
		this.components = components;
	}

	public Entity getEntity() {
		return entity;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> componentClass) {
		return (T) components.get(componentClass);
	}

}
