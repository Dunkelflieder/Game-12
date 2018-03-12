package game12.server.systems;

import game12.core.LogicSystem;
import game12.core.components.HealthComponent;
import game12.core.map.Entity;
import game12.core.networkEvents.DamageCollisionEvent;
import game12.core.networkEvents.HealthChangedEvent;
import game12.server.map.ServerMap;

public class HealthSystem extends LogicSystem {

	private ServerMap map;

	public HealthSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		getEventManager().register(DamageCollisionEvent.class, this::onHit);
	}

	private void onHit(DamageCollisionEvent event) {
		Entity entity = map.getEntity(event.entityID);
		if (entity == null) return;
		HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
		if (healthComponent == null) return;
		int oldHealth = healthComponent.health;

		healthComponent.health -= event.damage;
		if (healthComponent.health < 0) healthComponent.health = 0;
		final HealthChangedEvent healthChangedEvent = new HealthChangedEvent(
				entity.getID(),
				oldHealth,
				healthComponent.maxHealth,
				healthComponent.health,
				healthComponent.maxHealth
		);
		getEventManager().trigger(healthChangedEvent);
		map.getNetworkAdapter().send(healthChangedEvent);
		if (healthComponent.health == 0) {
			map.getEntityList().remove(entity.getID());
			// die
			// TODO
		}
	}
}
