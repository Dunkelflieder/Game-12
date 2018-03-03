package game12.core.components;

import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Bounding;
import de.nerogar.noise.util.BoundingAABB;
import de.nerogar.noise.util.BoundingSphere;
import de.nerogar.noise.util.Vector3f;
import game12.core.event.BoundingChangeEvent;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;
import game12.server.systems.PositionLookupSystem;

public class BoundingComponent extends Component {

	private Bounding bounding;

	public BoundingComponent() {
	}

	public BoundingComponent(Bounding bounding) {
		this.bounding = bounding;
	}

	@Override
	protected void initSystems() {
		refreshBounding();

		getEntity().getMap().getSystem(PositionLookupSystem.class).registerEntity(getEntity());
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) {
		String type = data.getStringUTF8("type");
		if (type.equals("sphere")) {
			bounding = new BoundingSphere(new Vector3f(), data.getFloat("radius"));
		} else if (type.equals("aabb")) {
			Vector3f size = new Vector3f(data.getFloat("sizeX"), data.getFloat("sizeY"), data.getFloat("sizeZ"));
			bounding = new BoundingAABB(new Vector3f(), size);
		}
	}

	public void refreshBounding() {
		PositionComponent positionComponent = getEntity().getComponent(PositionComponent.class);
		if (bounding instanceof BoundingSphere) {
			((BoundingSphere) bounding).setCenter(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
		} if (bounding instanceof BoundingAABB) {
			((BoundingAABB) bounding).setPosition(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
		}

		getEntity().getMap().getEventManager().trigger(new BoundingChangeEvent(this));
	}

	public Bounding getBounding() {
		return bounding;
	}

	@Override
	protected void cleanup() {
		getEntity().getMap().getSystem(PositionLookupSystem.class).unregisterEntity(getEntity());
	}

	@Override
	public Component clone() {
		return new BoundingComponent(bounding.clone());
	}

}
