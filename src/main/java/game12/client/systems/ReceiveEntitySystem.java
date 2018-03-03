package game12.client.systems;

import de.nerogar.noise.event.EventListener;
import game12.client.map.ClientMap;
import game12.core.Components;
import game12.core.EntityFactorySystem;
import game12.core.LogicSystem;
import game12.core.components.SynchronizedComponent;
import game12.core.map.Component;
import game12.core.network.packets.ComponentPacket;
import game12.core.networkEvents.EntityDespawnPacket;
import game12.core.networkEvents.EntitySpawnPacket;

import java.io.IOException;

public class ReceiveEntitySystem extends LogicSystem {

	private EventListener<EntitySpawnPacket>   spawnEntityListener;
	private EventListener<EntityDespawnPacket> despawnEntityListener;
	private EventListener<ComponentPacket>     componentListener;

	private ClientMap map;

	public ReceiveEntitySystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		spawnEntityListener = this::spawnEntityListenerFunction;
		despawnEntityListener = this::despawnEntityListenerFunction;
		componentListener = this::componentListenerFunction;

		getEventManager().register(EntitySpawnPacket.class, spawnEntityListener);
		getEventManager().register(EntityDespawnPacket.class, despawnEntityListener);
		getEventManager().register(ComponentPacket.class, componentListener);
	}

	private void spawnEntityListenerFunction(EntitySpawnPacket packet) {
		map.getSystem(EntityFactorySystem.class).createEntity(packet.getEntityID(), packet.getId(), packet.getX(), packet.getY(), packet.getZ());
	}

	private void despawnEntityListenerFunction(EntityDespawnPacket packet) {
		map.removeEntity(packet.getId());
	}

	private void componentListenerFunction(ComponentPacket packet) {
		Component component = map.getEntity(packet.getId()).getComponent(Components.getComponentByID(packet.getComponentID()));

		if (component instanceof SynchronizedComponent) {
			SynchronizedComponent synchronizedComponent = (SynchronizedComponent) component;

			try {
				synchronizedComponent.fromStream(packet.getInput());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(EntitySpawnPacket.class, spawnEntityListener);
		getEventManager().unregister(EntityDespawnPacket.class, despawnEntityListener);
		getEventManager().unregister(ComponentPacket.class, componentListener);
	}

}
