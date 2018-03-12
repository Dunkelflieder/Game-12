package game12.server.systems;

import de.nerogar.noise.util.Vector2f;
import de.nerogar.noise.util.Vector3f;
import game12.core.event.UpdateEvent;
import game12.core.networkEvents.EntityJumpEvent;
import game12.core.systems.MapSystem;
import game12.core.systems.PlayerSystem;
import game12.server.components.JumpBehaviorComponent;
import game12.server.map.ServerMap;

import java.util.Random;

public class JumpBehaviorSystem extends BehaviorSystem<JumpBehaviorComponent> {

	private Random random;

	private PlayerSystem playerSystem;
	private MapSystem    mapSystem;

	public JumpBehaviorSystem(ServerMap map) {
		super(JumpBehaviorComponent.class, map);
	}

	@Override
	public void init() {
		super.init();

		this.random = new Random();

		playerSystem = getContainer().getSystem(PlayerSystem.class);
		mapSystem = getContainer().getSystem(MapSystem.class);
	}

	private void nextTarget(JumpBehaviorComponent behavior) {

		Vector2f pos = new Vector2f(behavior.positionComponent.getX(), behavior.positionComponent.getZ());
		Vector2f playerPos = new Vector2f(playerSystem.getPlayerPosition().getX(), playerSystem.getPlayerPosition().getZ());

		float targetX;
		float targetY;

		if (pos.subtracted(playerPos).getSquaredValue() < behavior.maxDistance * behavior.maxDistance && random.nextFloat() < behavior.playerProbability) {
			targetX = playerPos.getX();
			targetY = playerPos.getY();
		} else {
			targetX = random.nextFloat() * (behavior.maxDistance * 2) - behavior.maxDistance + behavior.positionComponent.getX();
			targetY = random.nextFloat() * (behavior.maxDistance * 2) - behavior.maxDistance + behavior.positionComponent.getZ();
		}

		if (mapSystem.get((int) targetX, (int) targetY) == behavior.getOwnRoom() && mapSystem.isWalkable((int) targetX, (int) targetY, false)) {
			behavior.jumpTarget = new Vector3f(targetX, 0.0f, targetY);
			behavior.jumpSource = new Vector3f(behavior.positionComponent.getX(), 0.0f, behavior.positionComponent.getZ());

			getEventManager().trigger(new EntityJumpEvent(EntityJumpEvent.JUMP, behavior.getEntity().getID()));
			getNetworkAdapter().send(new EntityJumpEvent(EntityJumpEvent.JUMP, behavior.getEntity().getID()));
		}
	}

	@Override
	protected void behaviourFunction(UpdateEvent event, JumpBehaviorComponent behaviour) {
		float timeDelta = event.getDelta();
		behaviour.jumpTimer.update(timeDelta);

		if (behaviour.jumpTimer.trigger()) {
			behaviour.jumpTarget = null;
			behaviour.jumpProgress = 0;
		}

		if (behaviour.jumpTarget == null) {

			nextTarget(behaviour);

			if (behaviour.jumpTarget != null) {
				behaviour.jumpX = behaviour.jumpTarget.getX() - behaviour.jumpSource.getX();
				behaviour.jumpZ = behaviour.jumpTarget.getZ() - behaviour.jumpSource.getZ();
				float jumpDistance = (float) Math.sqrt(behaviour.jumpX * behaviour.jumpX + behaviour.jumpZ * behaviour.jumpZ);

				behaviour.totalJumpTime = jumpDistance / behaviour.speed;
				behaviour.jumpX /= behaviour.totalJumpTime;
				behaviour.jumpZ /= behaviour.totalJumpTime;

				behaviour.jumpParabolaA = JumpBehaviorComponent.GRAVITY / 2;
				behaviour.jumpParabolaB = -(JumpBehaviorComponent.GRAVITY * behaviour.totalJumpTime / 2);

				//System.out.println("f(x)=" + jumpParabolaA + "*x^2 + " + jumpParabolaB + "*x");
			}
		} else {
			if (behaviour.jumpProgress > behaviour.totalJumpTime) {
				behaviour.positionComponent.setPosition(
						behaviour.jumpTarget.getX(),
						behaviour.jumpTarget.getY(),
						behaviour.jumpTarget.getZ()
				                                       );

				behaviour.jumpProgress = behaviour.totalJumpTime;

				getEventManager().trigger(new EntityJumpEvent(EntityJumpEvent.IMPACT, behaviour.getEntity().getID()));
				getNetworkAdapter().send(new EntityJumpEvent(EntityJumpEvent.IMPACT, behaviour.getEntity().getID()));

			} else if (behaviour.jumpProgress < behaviour.totalJumpTime) {

				behaviour.jumpProgress += timeDelta;

				/*
				 * parabola ax^2 + bx + c
				 * with
				 *   a = gravity / 2
				 *   b = -(gravity * time / 2)
				 *   c = 0
				 */

				float x = behaviour.jumpProgress;

				behaviour.positionComponent.setPosition(
						behaviour.jumpSource.getX() + behaviour.jumpX * behaviour.jumpProgress,
						behaviour.jumpSource.getY() + (behaviour.jumpParabolaA * x * x) + (behaviour.jumpParabolaB * x),
						behaviour.jumpSource.getZ() + behaviour.jumpZ * behaviour.jumpProgress
				                                       );
			}
		}

	}
}
