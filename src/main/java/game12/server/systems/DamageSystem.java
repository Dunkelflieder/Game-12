package game12.server.systems;

import de.nerogar.noise.util.Vector3f;
import game12.core.components.ActorComponent;
import game12.core.components.BoundingComponent;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.Entity;
import game12.core.networkEvents.DamageAreaCollidingEvent;
import game12.core.systems.OnUpdateSystem;
import game12.server.components.DamageComponent;
import game12.server.map.ServerMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DamageSystem extends OnUpdateSystem {

	private final ServerMap            map;
	private       PositionLookupSystem positionLookupSystem;

	private Set<Entity> toRemove = new HashSet<>();

	public DamageSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		super.init();
		positionLookupSystem = getContainer().getSystem(PositionLookupSystem.class);
	}

	private void emitDamageAreaCollidingEvent(DamageComponent damageComponent, Entity targetEntity) {
		PositionComponent positionComponent = damageComponent.getEntity().getComponent(PositionComponent.class);
		DamageAreaCollidingEvent event = new DamageAreaCollidingEvent(
				damageComponent.damage,
				damageComponent.damageType,
				targetEntity.getID(),
				new Vector3f(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ())
		);
		map.getNetworkAdapter().send(event);
		getEventManager().trigger(event);
	}

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		for (Entity entity : toRemove) {
			if (entity.isValid()) map.removeEntity(entity.getID());
		}
		toRemove.clear();

		for (DamageComponent damageComponent : map.getEntityList().getComponents(DamageComponent.class)) {
			Entity entity = damageComponent.getEntity();
			BoundingComponent boundingComponent = entity.getComponent(BoundingComponent.class);
			Collection<BoundingComponent> hits = boundingComponent == null
					? positionLookupSystem.getBoundingsAround(entity, 0.01f)
					: positionLookupSystem.getBoundings(boundingComponent.getBounding());

			for (BoundingComponent hit : hits) {
				if (hit.getEntity().getID() == entity.getID()) continue;
				ActorComponent actor = hit.getEntity().getComponent(ActorComponent.class);
				if (actor == null) continue;
				if (actor.isPlayer == damageComponent.fromPlayer) continue;

				emitDamageAreaCollidingEvent(damageComponent, hit.getEntity());
				if (damageComponent.selfDestruct) toRemove.add(entity);
			}
		}
	}
}
