package strategy.client.systems;

import de.nerogar.noise.event.EventListener;
import strategy.client.map.ClientMap;
import strategy.core.Components;
import strategy.core.EntityFactorySystem;
import strategy.core.LogicSystem;
import strategy.core.components.SynchronizedComponent;
import strategy.core.map.Component;
import strategy.core.network.packets.ComponentPacket;
import strategy.core.networkEvents.EntityDespawnPacket;
import strategy.core.networkEvents.EntitySpawnPacket;

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
