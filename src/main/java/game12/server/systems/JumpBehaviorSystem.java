package game12.server.systems;

import game12.core.event.UpdateEvent;
import game12.core.systems.OnUpdateSystem;
import game12.server.components.JumpBehaviorComponent;
import game12.server.map.ServerMap;

public class JumpBehaviorSystem extends OnUpdateSystem {

	private final ServerMap map;

	public JumpBehaviorSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		for (JumpBehaviorComponent jumpBehaviorComponent : map.getEntityList().getComponents(JumpBehaviorComponent.class)) {
			jumpBehaviorComponent.update(event.getDelta());
		}
	}

}
