package game12.server.systems;

import de.nerogar.noise.util.Vector3f;
import game12.core.LogicSystem;
import game12.core.components.ActorComponent;
import game12.core.components.BoundingComponent;
import game12.core.components.PositionComponent;
import game12.core.components.ProjectileComponent;
import game12.core.event.UpdateEvent;
import game12.core.networkEvents.ProjectileHitEvent;
import game12.server.map.ServerMap;

import java.util.HashSet;
import java.util.Set;

public class ProjectileSystem extends LogicSystem {

	private final ServerMap            map;
	private       PositionLookupSystem positionLookupSystem;

	private Set<Integer> toRemove = new HashSet<>();

	public ProjectileSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		getEventManager().register(UpdateEvent.class, this::update);
		positionLookupSystem = getContainer().getSystem(PositionLookupSystem.class);
	}

	private void update(UpdateEvent event) {
		for (int entityID : toRemove) {
			if (map.getEntity(entityID) != null) {
				map.removeEntity(entityID);
			}
		}
		toRemove.clear();
		for (ProjectileComponent projectile : map.getEntityList().getComponents(ProjectileComponent.class)) {
			if (projectile.direction == null) continue;

			PositionComponent position = projectile.getEntity().getComponent(PositionComponent.class);
			position.setPosition(
					position.getX() + projectile.direction.getX() * projectile.speed * event.getDelta(),
					position.getY() + projectile.direction.getY() * projectile.speed * event.getDelta(),
					position.getZ() + projectile.direction.getZ() * projectile.speed * event.getDelta()
			                    );
			for (BoundingComponent hit : positionLookupSystem.getBoundingsAround(projectile.getEntity(), 0.01f)) {
				if (hit.getEntity().getID() == projectile.getEntity().getID()) continue;
				ActorComponent actor = hit.getEntity().getComponent(ActorComponent.class);
				if (actor == null) continue;
				if (actor.isPlayer == projectile.fromPlayer) continue;
				ProjectileHitEvent hitEvent = new ProjectileHitEvent(projectile, hit.getEntity().getID(), new Vector3f(position.getX(), position.getY(), position.getZ()));
				map.getNetworkAdapter().send(hitEvent);
				getEventManager().trigger(hitEvent);
				toRemove.add(projectile.getEntity().getID());
			}
		}
	}
}
