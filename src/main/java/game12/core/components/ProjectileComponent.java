package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

public class ProjectileComponent extends Component {

	public float speed;

	public Vector3f direction;

	public ProjectileComponent() {
		direction = new Vector3f();
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		speed = data.getFloat("speed");
	}

	public ProjectileComponent(float speed, Vector3f direction) {
		this.speed = speed;
		this.direction = direction;
	}

	@Override
	public ProjectileComponent clone() {
		return new ProjectileComponent(speed, direction.clone());
	}
}
