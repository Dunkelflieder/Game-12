package game12.server.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector2f;
import de.nerogar.noise.util.Vector3f;
import game12.core.EventTimer;
import game12.core.components.PositionComponent;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;
import game12.core.systems.MapSystem;
import game12.core.systems.PlayerSystem;

import java.util.Random;

public class JumpBehaviorComponent extends BehaviorComponent {

	private static final float GRAVITY = -20f;

	private float      jumpDelay;
	private EventTimer jumpTimer;
	private float      speed;
	private float      maxDistance;
	private float      playerProbability;

	private Vector3f jumpSource;
	private Vector3f jumpTarget;

	private float totalJumpTime;
	private float jumpProgress;
	private float jumpX;
	private float jumpZ;
	private float jumpParabolaA;
	private float jumpParabolaB;

	private MapSystem    mapSystem;
	private PlayerSystem playerSystem;

	private Random            random;
	private PositionComponent positionComponent;

	public JumpBehaviorComponent() {
	}

	public JumpBehaviorComponent(float jumpDelay, float speed, float maxDistance, float playerProbability) {
		this.jumpDelay = jumpDelay;
		jumpTimer = new EventTimer(jumpDelay, true);
		this.speed = speed;
		this.maxDistance = maxDistance;
		this.playerProbability = playerProbability;

	}

	@Override
	protected void init() {
		random = new Random();
		positionComponent = getEntity().getComponent(PositionComponent.class);
	}

	@Override
	protected void initSystems() {
		mapSystem = getEntity().getMap().getSystem(MapSystem.class);
		playerSystem = getEntity().getMap().getSystem(PlayerSystem.class);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		this.jumpDelay = data.getFloat("jumpDelay");
		this.speed = data.getFloat("speed");
		this.maxDistance = data.getFloat("maxDistance");
		this.playerProbability = data.getFloat("playerProbability");
	}

	private void nextTarget() {

		Vector2f pos = new Vector2f(positionComponent.getX(), positionComponent.getZ());
		Vector2f playerPos = new Vector2f(playerSystem.getPlayerPosition().getX(), playerSystem.getPlayerPosition().getZ());

		float targetX;
		float targetY;

		if (pos.subtracted(playerPos).getSquaredValue() < maxDistance * maxDistance && random.nextFloat() < 0.4) {
			targetX = playerPos.getX();
			targetY = playerPos.getY();
		} else {
			targetX = random.nextFloat() * (maxDistance * 2) - maxDistance + positionComponent.getX();
			targetY = random.nextFloat() * (maxDistance * 2) - maxDistance + positionComponent.getZ();
		}

		if (mapSystem.get((int) targetX, (int) targetY) == getOwnRoom() && mapSystem.isWalkable((int) targetX, (int) targetY, false)) {
			jumpTarget = new Vector3f(targetX, 0.0f, targetY);
			jumpSource = new Vector3f(positionComponent.getX(), 0.0f, positionComponent.getZ());
		}
	}

	public void update(float timeDelta) {
		jumpTimer.update(timeDelta);

		if (jumpTimer.trigger()) {
			jumpTarget = null;
			jumpProgress = 0;
		}

		if (jumpTarget == null) {

			nextTarget();

			if (jumpTarget != null) {
				jumpX = jumpTarget.getX() - jumpSource.getX();
				jumpZ = jumpTarget.getZ() - jumpSource.getZ();
				float jumpDistance = (float) Math.sqrt(jumpX * jumpX + jumpZ * jumpZ);

				totalJumpTime = jumpDistance / speed;
				jumpX /= totalJumpTime;
				jumpZ /= totalJumpTime;

				jumpParabolaA = GRAVITY / 2;
				jumpParabolaB = -(GRAVITY * totalJumpTime / 2);

				//System.out.println("f(x)=" + jumpParabolaA + "*x^2 + " + jumpParabolaB + "*x");

			}
		} else {
			if (jumpProgress > totalJumpTime) {
				positionComponent.setPosition(
						jumpTarget.getX(),
						jumpTarget.getY(),
						jumpTarget.getZ()
				                             );

				jumpProgress = totalJumpTime;

			} else if (jumpProgress < totalJumpTime) {

				jumpProgress += timeDelta;

				/*
				 * parabola ax^2 + bx + c
				 * with
				 *   a = gravity / 2
				 *   b = -(gravity * time / 2)
				 *   c = 0
				 */

				float x = jumpProgress;

				positionComponent.setPosition(
						jumpSource.getX() + jumpX * jumpProgress,
						jumpSource.getY() + (jumpParabolaA * x * x) + (jumpParabolaB * x),
						jumpSource.getZ() + jumpZ * jumpProgress
				                             );
			}
		}

	}

	@Override
	public Component clone() {
		return new JumpBehaviorComponent(jumpDelay, speed, maxDistance, playerProbability);
	}

}
