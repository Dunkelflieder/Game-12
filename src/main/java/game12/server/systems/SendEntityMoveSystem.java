package game12.server.systems;

import de.nerogar.noise.event.EventListener;
import game12.core.LogicSystem;
import game12.core.components.PositionComponent;
import game12.core.event.EntityMoveEvent;
import game12.core.map.Entity;
import game12.core.networkEvents.EntityMovePacket;
import game12.server.ServerMainThread;

public class SendEntityMoveSystem extends LogicSystem {

	private EventListener<EntityMoveEvent> moveListener;

	@Override
	public void init() {
		moveListener = this::moveListenerFunction;
		getEventManager().registerImmediate(EntityMoveEvent.class, moveListener);
	}

	private void moveListenerFunction(EntityMoveEvent event) {
		Entity entity = event.getEntity();
		PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
		EntityMovePacket packet = new EntityMovePacket(
				entity.getID(),
				1f / ServerMainThread.TICK_RATE,
				positionComponent.getX(),
				positionComponent.getY(),
				positionComponent.getZ(),
				positionComponent.getRotation(),
				positionComponent.getScale()
		);
		getNetworkAdapter().send(packet);
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(EntityMoveEvent.class, moveListener);
	}

}
