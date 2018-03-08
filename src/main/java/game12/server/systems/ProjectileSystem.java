package game12.server.systems;

import game12.core.LogicSystem;
import game12.core.components.ActorComponent;
import game12.core.components.BoundingComponent;
import game12.core.components.PositionComponent;
import game12.core.components.ProjectileComponent;
import game12.core.event.UpdateEvent;
import game12.server.event.ProjectileHitEvent;
import game12.server.map.ServerMap;

import java.util.HashSet;
import java.util.Set;

public class ProjectileSystem extends LogicSystem {

	private final ServerMap            map;
	private       PositionLookupSystem positionLookupSystem;

	public ProjectileSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		getEventManager().register(UpdateEvent.class, this::update);
		positionLookupSystem = getContainer().getSystem(PositionLookupSystem.class);
	}

	private void update(UpdateEvent event) {
		Set<Integer> toRemove = new HashSet<>();
		for (ProjectileComponent projectile : map.getEntityList().getComponents(ProjectileComponent.class)) {
			projectile.lifetime -= event.getDelta();
			if (projectile.lifetime <= 0) {
				toRemove.add(projectile.getEntity().getID());
				continue;
			}

			PositionComponent position = projectile.getEntity().getComponent(PositionComponent.class);
			position.setPosition(
					position.getX() + projectile.direction.getX() * projectile.speed *event.getDelta(),
					position.getY() + projectile.direction.getY() * projectile.speed * event.getDelta(),
					position.getZ() + projectile.direction.getZ() * projectile.speed * event.getDelta()
			                    );
			BoundingComponent bounding = projectile.getEntity().getComponent(BoundingComponent.class);
			for (BoundingComponent hit : positionLookupSystem.getBoundings(bounding.getBounding())) {
				if (hit.getEntity().getID() == projectile.getEntity().getID()) continue;
				ActorComponent actor = hit.getEntity().getComponent(ActorComponent.class);
				if (actor == null) continue;
				if (actor.isPlayer == projectile.fromPlayer) continue;
				ProjectileHitEvent hitEvent = new ProjectileHitEvent(projectile, hit.getEntity().getID());
				map.getNetworkAdapter().send(hitEvent);
				getEventManager().trigger(hitEvent);
				toRemove.add(projectile.getEntity().getID());
			}
		}
		toRemove.forEach(map.getEntityList()::remove);
	}
}
