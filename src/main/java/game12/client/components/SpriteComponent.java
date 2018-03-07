package game12.client.components;

import de.nerogar.noise.render.RenderProperties3f;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderable;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.client.map.ClientMap;
import game12.client.systems.SpriteSystem;
import game12.core.components.PositionComponent;
import game12.core.map.Component;
import game12.core.map.CoreMap;
import game12.core.systems.GameObjectsSystem;

public class SpriteComponent extends Component {

	private RenderProperties3f renderProperties;
	private DeferredRenderable renderable;

	private String skinId;

	private Vector3f forcedRotation;
	private Vector3f offset;

	public SpriteComponent() {
	}

	public SpriteComponent(String skinId, Vector3f forcedRotation, Vector3f offset) {
		this.skinId = skinId;
		this.forcedRotation = forcedRotation;
		this.offset = offset;
	}

	@Override
	protected void initSystems() {
		ClientMap clientMap = (ClientMap) getEntity().getMap();

		DeferredContainer deferredContainer = clientMap.getSystem(SpriteSystem.class).getDeferredContainer(skinId);

		PositionComponent position = getEntity().getComponent(PositionComponent.class);

		renderProperties = new RenderProperties3f(position.getRotation(), 0, 0, position.getX(), position.getY() * CoreMap.Y_FACTOR, position.getZ());
		renderProperties.setScale(position.getScale(), position.getScale(), position.getScale());
		renderable = new DeferredRenderable(deferredContainer, renderProperties);

		getEntity().getMap().getSystem(SpriteSystem.class).registerEntity(this);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) {
		skinId = data.getStringUTF8("skinId");

		if (data.contains("rotation")) {
			float[] directionArray = data.getFloatArray("rotation");
			forcedRotation = new Vector3f(directionArray[0], directionArray[1], directionArray[2]);
		}

		if (data.contains("offset")) {
			float[] offsetArray = data.getFloatArray("offset");
			offset = new Vector3f(offsetArray[0], offsetArray[1], offsetArray[2]);
		}

	}

	public DeferredRenderable getRenderable()              { return renderable; }

	public Vector3f getForcedRotation()                    { return forcedRotation; }

	public void setForcedRotation(Vector3f forcedRotation) { this.forcedRotation = forcedRotation; }

	public Vector3f getOffset()                            { return offset; }

	public void updatePosition(float newX, float newY, float newZ, float newScale) {
		renderProperties.setXYZ(newX, newY * CoreMap.Y_FACTOR, newZ);
		renderProperties.setScale(newScale, newScale, newScale);
	}

	public void updateRotation(float yaw, float pitch, float roll) {
		renderProperties.setYaw(yaw);
		renderProperties.setPitch(pitch);
		renderProperties.setRoll(roll);
	}

	public String getSkinId() {
		return skinId;
	}

	@Override
	protected void cleanup() {
		getEntity().getMap().getSystem(SpriteSystem.class).unregisterEntity(this);
	}

	@Override
	public Component clone() {
		return new SpriteComponent(
				skinId,
				forcedRotation == null ? null : forcedRotation.clone(),
				offset == null ? null : offset.clone()
		);
	}

}