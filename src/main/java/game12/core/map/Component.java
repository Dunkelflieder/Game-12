package game12.core.map;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import game12.core.systems.GameObjectsSystem;

public abstract class Component implements Cloneable {

	private Entity entity;

	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
	}

	protected final void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() { return entity; }

	protected void initSystems() {
	}

	protected void init() {
	}

	protected void cleanup() {
	}

	@Override
	public abstract Component clone();

}
