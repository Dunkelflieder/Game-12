package game12.server.systems;

import de.nerogar.noise.util.Vector2f;
import de.nerogar.noise.util.Vector3f;
import game12.core.EntityFactorySystem;
import game12.core.components.PositionComponent;
import game12.core.components.ProjectileComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.Entity;
import game12.core.systems.GameObjectsSystem;
import game12.core.systems.MapSystem;
import game12.server.components.TurretBossBehaviorComponent;
import game12.server.map.ServerMap;

import java.util.Random;

public class TurretBossBehaviorSystem extends BehaviorSystem<TurretBossBehaviorComponent> {

	private short projectileBlueprintId;

	private EntityFactorySystem entityFactory;

	private Random    random;
	private MapSystem mapSystem;

	public TurretBossBehaviorSystem(ServerMap map) {
		super(TurretBossBehaviorComponent.class, map);
	}

	@Override
	public void init() {
		super.init();

		this.random = new Random();

		projectileBlueprintId = map.getGameSystem(GameObjectsSystem.class).getID("turret-projectile");

		entityFactory = map.getSystem(EntityFactorySystem.class);
		mapSystem = map.getSystem(MapSystem.class);
	}

	private Vector2f nextPosition(PositionComponent position, TurretBossBehaviorComponent behavior) {
		while (true) {
			float targetX = random.nextFloat() * 10 - 5 + position.getX();
			float targetY = random.nextFloat() * 10 - 5 + position.getZ();

			if (mapSystem.get((int) targetX, (int) targetY) == behavior.getOwnRoom() && mapSystem.isWalkable((int) targetX, (int) targetY, false)) {
				return new Vector2f(targetX, targetY);
			}
		}
	}

	@Override
	protected void behaviourFunction(UpdateEvent event, TurretBossBehaviorComponent behaviour) {
		behaviour.moveTimer.update(event.getDelta());
		behaviour.shootTimer.update(event.getDelta());

		PositionComponent position = behaviour.getEntity().getComponent(PositionComponent.class);

		if (behaviour.moveDirection != 0) {
			float moveDistance = 3f;

			behaviour.moveState += event.getDelta() * behaviour.moveDirection;

			position.setPosition(position.getX(), behaviour.moveState * moveDistance, position.getZ());

			if (behaviour.moveDirection < 0 && behaviour.moveState < -1) {
				behaviour.moveDirection = 1;
				behaviour.moveState = -1;

				Vector2f nextPosition = nextPosition(position, behaviour);

				position.setPosition(nextPosition.getX(), -moveDistance, nextPosition.getY());
			} else if (behaviour.moveDirection > 0 && behaviour.moveState > 0) {
				behaviour.moveDirection = 0;
				behaviour.moveState = 0;

				behaviour.resetShootTimer();
			}
		}

		if (behaviour.moveTimer.trigger()) {
			behaviour.moveDirection = -1;
		}

		while (behaviour.shootTimer.trigger()) {
			int fireballCount = 8;

			Vector3f playerPosition = playerSystem.getPlayerPosition().setY(0.5f);

			float h = 2f;

			float playerDistanceX = position.getX() - playerPosition.getX();
			float playerDistanceY = position.getZ() - playerPosition.getZ();
			float playerDistance = (float) Math.sqrt(playerDistanceX * playerDistanceX + playerDistanceY * playerDistanceY);

			float ySpeed = h / playerDistance;

			float randomAngle = (float) Math.random();

			for (int i = 1; i <= fireballCount; i++) {
				Entity projectile = entityFactory.createEntity(projectileBlueprintId, position.getX(), position.getY() + h, position.getZ());

				float angle = (float) (Math.PI * 2f * (i + randomAngle) / fireballCount);

				projectile.getComponent(ProjectileComponent.class).direction.set((float) Math.sin(angle), -ySpeed, (float) Math.cos(angle)).normalize();
			}

		}
	}

}
