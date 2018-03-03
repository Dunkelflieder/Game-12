package game12.server.systems;

import de.nerogar.noise.event.EventListener;
import game12.core.LogicSystem;
import game12.core.components.PositionComponent;
import game12.core.event.EntityDespawnEvent;
import game12.core.event.EntitySpawnEvent;
import game12.core.map.Entity;
import game12.core.network.packets.ComponentPacket;
import game12.core.networkEvents.EntityDespawnPacket;
import game12.core.networkEvents.EntitySpawnPacket;
import game12.server.event.ComponentUpdateEvent;

public class SendEntitySystem extends LogicSystem {

	private EventListener<EntitySpawnEvent>     spawnEntityListener;
	private EventListener<EntityDespawnEvent>   despawnEntityListener;
	private EventListener<ComponentUpdateEvent> componentUpdateListener;

	@Override
	public void init() {
		spawnEntityListener = this::spawnEntityListenerFunction;
		despawnEntityListener = this::despawnEntityListenerFunction;
		componentUpdateListener = this::componentUpdateListenerFunction;

		getEventManager().registerImmediate(EntitySpawnEvent.class, spawnEntityListener);
		getEventManager().registerImmediate(EntityDespawnEvent.class, despawnEntityListener);
		getEventManager().registerImmediate(ComponentUpdateEvent.class, componentUpdateListener);
	}

	private void spawnEntityListenerFunction(EntitySpawnEvent event) {
		Entity entity = event.getEntity();
		PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
		EntitySpawnPacket packet = new EntitySpawnPacket(entity.getEntityID(), entity.getID(), positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
		getNetworkAdapter().send(packet);
	}

	private void despawnEntityListenerFunction(EntityDespawnEvent event) {
		Entity entity = event.getEntity();
		EntityDespawnPacket packet = new EntityDespawnPacket(entity.getID());
		getNetworkAdapter().send(packet);
	}

	private void componentUpdateListenerFunction(ComponentUpdateEvent event) {
		getNetworkAdapter().send(new ComponentPacket(event.getComponent()));
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(EntitySpawnEvent.class, spawnEntityListener);
		getEventManager().unregister(EntityDespawnEvent.class, despawnEntityListener);
		getEventManager().unregister(ComponentUpdateEvent.class, componentUpdateListener);
	}

}
