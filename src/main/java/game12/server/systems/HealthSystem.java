package game12.server.systems;

import de.nerogar.noise.util.Vector3f;
import game12.core.components.HealthComponent;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.Entity;
import game12.core.networkEvents.DamageAreaCollidingEvent;
import game12.core.networkEvents.DamageImpactEvent;
import game12.core.networkEvents.HealthChangedEvent;
import game12.core.systems.OnUpdateSystem;
import game12.server.map.ServerMap;

import java.util.HashSet;
import java.util.Set;

public class HealthSystem extends OnUpdateSystem {

	private ServerMap map;

	private Set<Entity> toRemove = new HashSet<>();

	public HealthSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		super.init();
		getEventManager().register(DamageAreaCollidingEvent.class, this::onHit);
	}

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		for (HealthComponent healthComponent : map.getEntityList().getComponents(HealthComponent.class)) {
			if (healthComponent.invulnerability > event.getDelta()) {
				healthComponent.invulnerability -= event.getDelta();
			} else if (healthComponent.invulnerability != 0) {
				healthComponent.invulnerability = 0;
				HealthChangedEvent healthChangedEvent = new HealthChangedEvent(
						healthComponent.getEntity().getID(),
						healthComponent.health,
						healthComponent.maxHealth,
						healthComponent.health,
						healthComponent.maxHealth,
						true,
						false
				);
				getEventManager().trigger(healthChangedEvent);
				map.getNetworkAdapter().send(healthChangedEvent);
			}
		}
	}

	private void emitDamageImpactEvent(Vector3f damagePosition, Entity targetEntity) {
		PositionComponent targetPositionComponent = targetEntity.getComponent(PositionComponent.class);
		Vector3f impactDir = new Vector3f(
				targetPositionComponent.getX() - damagePosition.getX(),
				targetPositionComponent.getY() - damagePosition.getY(),
				targetPositionComponent.getZ() - damagePosition.getZ()
		);
		if (impactDir.getSquaredValue() == 0) {
			// prevent NaN
			impactDir = new Vector3f(1);
		}
		impactDir.normalize();
		DamageImpactEvent event = new DamageImpactEvent(
				targetEntity.getID(),
				damagePosition,
				impactDir
		);
		map.getNetworkAdapter().send(event);
		getEventManager().trigger(event);
	}

	private void onHit(DamageAreaCollidingEvent event) {
		for (Entity entity : toRemove) {
			if (entity.isValid()) map.removeEntity(entity.getID());
		}
		toRemove.clear();

		Entity entity = map.getEntity(event.entityID);
		if (entity == null) return;
		HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
		if (healthComponent == null) return;
		int damage = event.damage;
		if (healthComponent.invulnerability > 0) {
			damage = 0;
		} else if (entity.hasComponent(HealthComponent.class)) {
			// TODO decide smarter what gets invulnerability and what doesn't
			healthComponent.invulnerability = 0.5f;
		}

		int oldHealth = healthComponent.health;

		healthComponent.health -= damage;
		if (healthComponent.health < 0) healthComponent.health = 0;
		final HealthChangedEvent healthChangedEvent = new HealthChangedEvent(
				entity.getID(),
				oldHealth,
				healthComponent.maxHealth,
				healthComponent.health,
				healthComponent.maxHealth,
				false,
				healthComponent.invulnerability > 0
		);
		getEventManager().trigger(healthChangedEvent);
		map.getNetworkAdapter().send(healthChangedEvent);

		emitDamageImpactEvent(event.damagePosition, entity);

		if (healthComponent.health == 0) {
			toRemove.add(entity);
			// TODO death animations?
		}
	}
}
