package strategy.server.systems;

import de.nerogar.noise.event.EventListener;
import strategy.core.LogicSystem;
import strategy.core.components.PositionComponent;
import strategy.core.event.EntityMoveEvent;
import strategy.core.map.Entity;
import strategy.core.networkEvents.EntityMovePacket;
import strategy.server.ServerMainThread;

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
