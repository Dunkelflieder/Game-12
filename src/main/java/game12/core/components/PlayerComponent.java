package game12.core.components;

import de.nerogar.noise.serialization.NDSNodeObject;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

public class PlayerComponent extends Component {

	public PlayerComponent() {
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) {
	}

	@Override
	protected void initSystems() {
	}

	@Override
	protected void cleanup() {
	}

	@Override
	public Component clone() {
		return new PlayerComponent();
	}

}
