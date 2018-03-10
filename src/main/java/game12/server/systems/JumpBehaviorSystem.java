package game12.server.systems;

import game12.core.event.UpdateEvent;
import game12.server.components.JumpBehaviorComponent;
import game12.server.map.ServerMap;

public class JumpBehaviorSystem extends BehaviorSystem<JumpBehaviorComponent> {

	public JumpBehaviorSystem(ServerMap map) {
		super(JumpBehaviorComponent.class, map);
	}

	@Override
	protected void behaviourFunction(UpdateEvent event, JumpBehaviorComponent behaviour) {
		behaviour.update(event.getDelta());
	}
}
