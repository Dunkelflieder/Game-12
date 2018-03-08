package game12.client.systems;

import game12.client.map.ClientMap;
import game12.core.LogicSystem;
import game12.server.event.DamageEvent;
import game12.server.event.ProjectileHitEvent;

public class ParticleSystem extends LogicSystem {

	ClientMap map;

	public ParticleSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		getEventManager().register(ProjectileHitEvent.class, event -> {
			System.out.println("Event collision: " + event.entityID);
		});
		getEventManager().register(DamageEvent.class, event -> {
			System.out.println("Damage taken: " + event.entityID + " from " + event.oldHealth + " to " + event.newHealth);
		});
	}

}
