package game12.server.systems;

import game12.core.LogicSystem;
import game12.core.components.ActorComponent;
import game12.core.components.BoundingComponent;
import game12.core.components.PositionComponent;
import game12.core.components.ProjectileComponent;
import game12.core.event.UpdateEvent;
import game12.server.event.ProjectileHitEvent;
import game12.server.map.ServerMap;

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
		for (ProjectileComponent projectile : map.getEntityList().getComponents(ProjectileComponent.class)) {
			PositionComponent position = projectile.getEntity().getComponent(PositionComponent.class);
			position.setPosition(
					position.getX() + projectile.direction.getX() * event.getDelta(),
					position.getY() + projectile.direction.getY() * event.getDelta(),
					position.getZ() + projectile.direction.getZ() * event.getDelta()
			                    );
			BoundingComponent bounding = projectile.getEntity().getComponent(BoundingComponent.class);
			boolean didCollide = false;
			for (BoundingComponent hit : positionLookupSystem.getBoundings(bounding.getBounding())) {
				if (hit.getEntity().getID() == projectile.getEntity().getID()) continue;
				ActorComponent actor = hit.getEntity().getComponent(ActorComponent.class);
				if (actor == null) continue;
				if (actor.isPlayer == projectile.fromPlayer) continue;
				ProjectileHitEvent hitEvent = new ProjectileHitEvent(hit.getEntity().getID());
				map.getNetworkAdapter().send(hitEvent);
				getEventManager().trigger(hitEvent);
				didCollide = true;
			}
			if (didCollide) {
				map.getEntityList().remove(projectile.getEntity().getID());
			}
		}
	}
}
