package game12.server.systems.behaviors;

import game12.core.event.UpdateEvent;
import game12.core.systems.OnUpdateSystem;
import game12.core.systems.PlayerSystem;
import game12.server.components.BehaviorComponent;
import game12.server.map.ServerMap;

import java.util.ArrayList;
import java.util.List;

public abstract class BehaviorSystem<T extends BehaviorComponent> extends OnUpdateSystem {

	private final Class<T> behaviourClass;

	protected final ServerMap    map;
	protected       PlayerSystem playerSystem;

	public BehaviorSystem(Class<T> behaviourClass, ServerMap map) {
		this.behaviourClass = behaviourClass;
		this.map = map;
	}

	@Override
	public void init() {
		super.init();
		playerSystem = getContainer().getSystem(PlayerSystem.class);
	}

	protected abstract void behaviourFunction(UpdateEvent event, T behaviour);

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		List<T> components = new ArrayList<>();
		for (T behaviorComponent : map.getEntityList().getComponents(behaviourClass)) {
			if (behaviorComponent.getOwnRoom() == playerSystem.getPlayerRoomID()) {
				components.add(behaviorComponent);
			}
		}
		// TODO avoid concurrent modification exceptions more elegantly and performable
		for (T component : components) {
			behaviourFunction(event, component);
		}
	}
}
