package game12.server.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.core.EventTimer;
import game12.core.components.PositionComponent;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;
import game12.core.systems.MapSystem;

import java.util.Random;

public class JumpBehaviorComponent extends Component {

	private static final float GRAVITY    = -20f;
	private static final float JUMP_SPEED = 5.0f;

	private float      jumpTime;
	private EventTimer jumpTimer;
	private Vector3f   jumpSource;
	private Vector3f   jumpTarget;

	private float totalJumpTime;
	private float jumpProgress;
	private float jumpX;
	private float jumpZ;
	private float jumpParabolaA;
	private float jumpParabolaB;

	private MapSystem mapSystem;
	private int ownRoom = -1;

	private Random            random;
	private PositionComponent positionComponent;

	public JumpBehaviorComponent() {
	}

	public JumpBehaviorComponent(float jumpTime) {
		this.jumpTime = jumpTime;
		jumpTimer = new EventTimer(jumpTime, true);
	}

	@Override
	protected void init() {
		random = new Random();
		positionComponent = getEntity().getComponent(PositionComponent.class);
	}

	@Override
	protected void initSystems() {
		mapSystem = getEntity().getMap().getSystem(MapSystem.class);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		this.jumpTime = data.getFloat("jumpTime");
	}

	public void setOwnRoom(int ownRoom) {
		this.ownRoom = ownRoom;
	}

	public void update(float timeDelta) {
		jumpTimer.update(timeDelta);

		if (jumpTimer.trigger()) {
			jumpTarget = null;
			jumpProgress = 0;
		}

		if (jumpTarget == null) {
			float targetX = random.nextFloat() * 6 - 3 + positionComponent.getX();
			float targetY = random.nextFloat() * 6 - 3 + positionComponent.getZ();

			if (mapSystem.get((int) targetX, (int) targetY) == ownRoom && mapSystem.isWalkable((int) targetX, (int) targetY, false)) {
				jumpTarget = new Vector3f(targetX + 0.5f, 0.0f, targetY + 0.5f);
				jumpSource = new Vector3f(positionComponent.getX(), 0.0f, positionComponent.getZ());

				jumpX = jumpTarget.getX() - jumpSource.getX();
				jumpZ = jumpTarget.getZ() - jumpSource.getZ();
				float jumpDistance = (float) Math.sqrt(jumpX * jumpX + jumpZ * jumpZ);

				totalJumpTime = jumpDistance / JUMP_SPEED;

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
		return new JumpBehaviorComponent(jumpTime);
	}

}
