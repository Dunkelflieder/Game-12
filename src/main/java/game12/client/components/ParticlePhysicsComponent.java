package game12.client.components;

import de.nerogar.noise.util.Vector3f;
import game12.core.map.Component;

/**
 * Is affected by gravity and doesn't intersect with the map
 */
public class ParticlePhysicsComponent extends Component {

	public static final Vector3f DEFAULT_GRAVITY = new Vector3f(0, -10f, 0);

	public Vector3f velocity;
	public Vector3f gravity;

	public ParticlePhysicsComponent() {
		velocity = new Vector3f();
		gravity = DEFAULT_GRAVITY;
	}

	public ParticlePhysicsComponent(Vector3f velocity, Vector3f gravity) {
		this.velocity = velocity;
		this.gravity = gravity;
	}

	public void update(float timeDelta) {
		velocity.addX(gravity.getX() * timeDelta);
		velocity.addY(gravity.getY() * timeDelta);
		velocity.addZ(gravity.getZ() * timeDelta);
	}

	@Override
	public Component clone() {
		return new ParticlePhysicsComponent(velocity.clone(), gravity.clone());
	}
}
