package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

@ComponentInfo(name = "actor", side = ComponentSide.CORE)
public class ActorComponent extends Component {

	public boolean isPlayer;

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		isPlayer = data.getBoolean("isPlayer");
	}

	@Override
	public Component clone() {
		ActorComponent component = new ActorComponent();
		component.isPlayer = isPlayer;
		return component;
	}
}
