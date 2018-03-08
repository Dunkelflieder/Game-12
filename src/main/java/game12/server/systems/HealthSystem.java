package game12.server.systems;

import game12.core.LogicSystem;
import game12.core.components.HealthComponent;
import game12.core.map.Entity;
import game12.server.event.DamageEvent;
import game12.server.event.ProjectileHitEvent;
import game12.server.map.ServerMap;

public class HealthSystem extends LogicSystem {

	private ServerMap map;

	public HealthSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		getEventManager().register(ProjectileHitEvent.class, this::onHit);
	}

	private void onHit(ProjectileHitEvent event) {
		Entity entity = map.getEntity(event.entityID);
		if (entity == null) return;
		HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
		if (healthComponent == null) return;
		float oldHealth = healthComponent.health;

		healthComponent.health -= event.projectile.damage;
		if (healthComponent.health <= 0) {
			map.getEntityList().remove(entity.getID());
			// die
			// TODO
		} else {
			DamageEvent damageEvent = new DamageEvent(entity.getID(), oldHealth, healthComponent.health);
			getEventManager().trigger(damageEvent);
			map.getNetworkAdapter().send(damageEvent);
		}
	}
}
