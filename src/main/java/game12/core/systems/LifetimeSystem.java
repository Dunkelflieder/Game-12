package game12.core.systems;

import game12.core.LogicSystem;
import game12.core.components.LifetimeComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.CoreMap;

import java.util.HashSet;
import java.util.Set;

public class LifetimeSystem extends LogicSystem {

	CoreMap map;

	public LifetimeSystem(CoreMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		getEventManager().register(UpdateEvent.class, this::update);
	}

	public void update(UpdateEvent event) {
		Set<Integer> toRemove = new HashSet<>();
		for (LifetimeComponent lifetimeComponent : map.getEntityList().getComponents(LifetimeComponent.class)) {
			if (lifetimeComponent.lifetime <= 0) {
				toRemove.add(lifetimeComponent.getEntity().getID());
			} else {
				lifetimeComponent.lifetime -= event.getDelta();
			}
		}
		toRemove.forEach(map.getEntityList()::remove);
	}

}
