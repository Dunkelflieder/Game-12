package game12.server.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.core.EventTimer;
import game12.core.components.PositionComponent;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

public class JumpBehaviorComponent extends BehaviorComponent {

	public static final float GRAVITY = -20f;

	private float      jumpDelay;
	public  EventTimer jumpTimer;
	public  float      speed;
	public  float      maxDistance;
	public  float      playerProbability;
	public  int        impactDamage;

	public Vector3f jumpSource;
	public Vector3f jumpTarget;

	public float totalJumpTime;
	public float jumpProgress;
	public float jumpX;
	public float jumpZ;
	public float jumpParabolaA;
	public float jumpParabolaB;

	public PositionComponent positionComponent;

	public JumpBehaviorComponent() {
	}

	public JumpBehaviorComponent(float jumpDelay, float speed, float maxDistance, float playerProbability, int impactDamage) {
		this.jumpDelay = jumpDelay;
		jumpTimer = new EventTimer(jumpDelay, true);
		this.speed = speed;
		this.maxDistance = maxDistance;
		this.playerProbability = playerProbability;
		this.impactDamage = impactDamage;

	}

	@Override
	protected void init() {
		positionComponent = getEntity().getComponent(PositionComponent.class);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		this.jumpDelay = data.getFloat("jumpDelay");
		this.speed = data.getFloat("speed");
		this.maxDistance = data.getFloat("maxDistance");
		this.playerProbability = data.getFloat("playerProbability");
		if (data.contains("impactDamage")) {
			impactDamage = data.getInt("impactDamage");
		} else {
			impactDamage = 0;
		}
	}

	@Override
	public Component clone() {
		return new JumpBehaviorComponent(jumpDelay, speed, maxDistance, playerProbability, impactDamage);
	}

}
