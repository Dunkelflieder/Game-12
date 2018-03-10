package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

public class LifetimeComponent extends Component {

	public float initialLifetime;
	public float lifetime;

	public LifetimeComponent() {
	}

	public LifetimeComponent(float lifetime) {
		this.initialLifetime = lifetime;
		this.lifetime = lifetime;
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		this.initialLifetime = data.getFloat("lifetime");
		this.lifetime = this.initialLifetime;
	}

	public void update(float timeDelta) {
		lifetime -= timeDelta;
	}

	@Override
	public Component clone() {
		return new LifetimeComponent(initialLifetime);
	}
}
