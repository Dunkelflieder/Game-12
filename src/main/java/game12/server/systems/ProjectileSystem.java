package game12.server.systems;

import game12.core.components.PositionComponent;
import game12.core.components.ProjectileComponent;
import game12.core.event.UpdateEvent;
import game12.core.systems.OnUpdateSystem;
import game12.server.map.ServerMap;

public class ProjectileSystem extends OnUpdateSystem {

	private final ServerMap map;

	public ProjectileSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		for (ProjectileComponent projectile : map.getEntityList().getComponents(ProjectileComponent.class)) {
			if (projectile.direction == null) continue;
			PositionComponent position = projectile.getEntity().getComponent(PositionComponent.class);
			position.setPosition(
					position.getX() + projectile.direction.getX() * projectile.speed * event.getDelta(),
					position.getY() + projectile.direction.getY() * projectile.speed * event.getDelta(),
					position.getZ() + projectile.direction.getZ() * projectile.speed * event.getDelta()
			                    );
		}
	}
}
