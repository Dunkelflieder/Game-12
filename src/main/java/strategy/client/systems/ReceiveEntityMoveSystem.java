package strategy.client.systems;

import de.nerogar.noise.event.EventListener;
import strategy.client.components.InterpolatePositionComponent;
import strategy.client.map.ClientMap;
import strategy.core.LogicSystem;
import strategy.core.components.PositionComponent;
import strategy.core.event.UpdateEvent;
import strategy.core.map.Entity;
import strategy.core.networkEvents.EntityMovePacket;

public class ReceiveEntityMoveSystem extends LogicSystem {

	private EventListener<EntityMovePacket> moveListener;
	private EventListener<UpdateEvent>      updateListener;

	private ClientMap map;

	public ReceiveEntityMoveSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		moveListener = this::moveListenerFunction;
		updateListener = this::updateListenerFunction;

		getEventManager().register(EntityMovePacket.class, moveListener);
		getEventManager().register(UpdateEvent.class, updateListener);
	}

	private void moveListenerFunction(EntityMovePacket packet) {
		Entity entity = map.getEntity(packet.getEntityID());

		InterpolatePositionComponent interpolateComponent = entity.getComponent(InterpolatePositionComponent.class);

		if (interpolateComponent != null) {
			interpolateComponent.newMovement(packet.getTime(), packet.getX(), packet.getY(), packet.getZ(), packet.getRotation(), packet.getScale());
		} else {
			PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
			positionComponent.setPosition(packet.getX(), packet.getY(), packet.getZ(), packet.getRotation(), packet.getScale());
		}

	}

	private void updateListenerFunction(UpdateEvent event) {
		for (InterpolatePositionComponent interpolatePositionComponent : map.getEntityList().getComponents(InterpolatePositionComponent.class)) {
			interpolatePositionComponent.update(event.getDelta());
		}
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(EntityMovePacket.class, moveListener);
		getEventManager().unregister(UpdateEvent.class, updateListener);
	}

}
