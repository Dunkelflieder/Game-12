package game12.core.systems;

import game12.core.components.SimpleOnUpdateComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.CoreMap;

public class SimpleOnUpdateSystem extends OnUpdateSystem {

	private CoreMap map;

	public SimpleOnUpdateSystem(CoreMap map) {
		this.map = map;
	}

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		for (SimpleOnUpdateComponent component : map.getEntityList().getComponents(SimpleOnUpdateComponent.class)) {
			component.update(event);
		}
	}
}
