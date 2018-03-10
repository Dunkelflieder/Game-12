package game12.server.systems;

import game12.core.LogicSystem;
import game12.core.components.PositionComponent;
import game12.core.event.EntitySpawnEvent;
import game12.core.map.Component;
import game12.core.systems.MapSystem;
import game12.server.components.BehaviorComponent;
import game12.server.map.ServerMap;

public class SetBehaviorRoomSystem extends LogicSystem {

	private ServerMap map;
	private MapSystem mapSystem;

	public SetBehaviorRoomSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		super.init();

		mapSystem = map.getSystem(MapSystem.class);
		getEventManager().registerImmediate(EntitySpawnEvent.class, this::onEntitySpawn);
	}

	private void onEntitySpawn(EntitySpawnEvent event) {
		for (Component component : event.getEntity().getComponents()) {
			if (component instanceof BehaviorComponent) {
				PositionComponent positionComponent = event.getEntity().getComponent(PositionComponent.class);
				((BehaviorComponent) component).setOwnRoom(mapSystem.get((int) positionComponent.getX(), (int) positionComponent.getZ()));
			}
		}
	}

}
