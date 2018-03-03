package game12.client.systems;

import de.nerogar.noise.event.EventListener;
import game12.client.components.InterpolatePositionComponent;
import game12.client.map.ClientMap;
import game12.core.LogicSystem;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.Entity;
import game12.core.networkEvents.EntityMovePacket;

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
