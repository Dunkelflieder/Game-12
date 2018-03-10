package game12.server.systems;

import de.nerogar.noise.util.Vector3f;
import game12.core.EntityFactorySystem;
import game12.core.EventTimer;
import game12.core.components.LifetimeComponent;
import game12.core.components.PositionComponent;
import game12.core.components.ProjectileComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.Entity;
import game12.core.systems.GameObjectsSystem;
import game12.core.systems.PlayerSystem;
import game12.server.components.TurretBehaviorComponent;
import game12.server.map.ServerMap;

public class TurretBehaviorSystem extends BehaviorSystem<TurretBehaviorComponent> {

	private final ServerMap    map;
	private       short        projectileBlueprintId;
	private       PlayerSystem playerSystem;

	public TurretBehaviorSystem(ServerMap map) {
		super(TurretBehaviorComponent.class, map);
		this.map = map;
	}

	@Override
	public void init() {
		super.init();
		projectileBlueprintId = map.getGameSystem(GameObjectsSystem.class).getID("turret-projectile");
		playerSystem = getContainer().getSystem(PlayerSystem.class);
	}

	@Override
	protected void behaviourFunction(UpdateEvent event, TurretBehaviorComponent turretComponent) {
		Entity turret = turretComponent.getEntity();

		EventTimer shootTimer = turretComponent.shootTimer;

		shootTimer.update(event.getDelta());
		if (shootTimer.trigger()) {
			turretComponent.projectile = map.getSystem(EntityFactorySystem.class)
					.createEntity(
							projectileBlueprintId,
							turret.getComponent(PositionComponent.class).getX(),
							turret.getComponent(PositionComponent.class).getY(),
							turret.getComponent(PositionComponent.class).getZ()
					             );
			turretComponent.shootDelay = TurretBehaviorComponent.MAX_SHOOT_DELAY;
		}

		if (turretComponent.projectile != null) {
			if (!turretComponent.projectile.isValid()) {
				turretComponent.projectile = null;
				return;
			}

			turretComponent.shootDelay -= event.getDelta();

			turretComponent.projectile.getComponent(PositionComponent.class).setPosition(
					turret.getComponent(PositionComponent.class).getX(),
					turret.getComponent(PositionComponent.class).getY() + 1.2f,
					turret.getComponent(PositionComponent.class).getZ()
			                                                                            );
			turretComponent.projectile.getComponent(PositionComponent.class).setScale(1f - (turretComponent.shootDelay / TurretBehaviorComponent.MAX_SHOOT_DELAY));

			if (turretComponent.shootDelay <= 0) {
				PositionComponent projectilePosition = turretComponent.projectile.getComponent(PositionComponent.class);

				Vector3f direction = playerSystem.getPlayerPosition();
				direction.addX(-projectilePosition.getX());
				direction.addY(-projectilePosition.getY());
				direction.addZ(-projectilePosition.getZ());
				direction.normalize();

				turretComponent.projectile.getComponent(ProjectileComponent.class).direction = direction;

				turretComponent.projectile = null;
			} else {
				LifetimeComponent lifetimeComponent = turretComponent.projectile.getComponent(LifetimeComponent.class);
				lifetimeComponent.lifetime = lifetimeComponent.initialLifetime;
			}

		}
	}

}
