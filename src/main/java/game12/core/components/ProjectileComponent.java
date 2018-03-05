package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProjectileComponent extends SynchronizedComponent {

	public float   speed;
	public float   lifetime;
	public boolean fromPlayer;

	public Vector3f direction;

	public ProjectileComponent() {
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		speed = data.getFloat("speed");
		lifetime = data.getFloat("lifetime");
	}

	public ProjectileComponent(float speed, float lifetime, boolean fromPlayer, Vector3f direction) {
		this.speed = speed;
		this.lifetime = lifetime;
		this.fromPlayer = fromPlayer;
		this.direction = direction;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
	}

	@Override
	public Component clone() {
		return new ProjectileComponent(speed, lifetime, fromPlayer, direction);
	}
}
