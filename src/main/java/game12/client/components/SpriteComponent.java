package game12.client.components;

import de.nerogar.noise.render.RenderProperties3f;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderable;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.client.systems.SpriteSystem;
import game12.core.components.PositionComponent;
import game12.core.map.Component;
import game12.core.map.CoreMap;
import game12.core.systems.GameObjectsSystem;

public class SpriteComponent extends Component {

	private RenderProperties3f renderProperties;
	private DeferredRenderable renderable;

	private String skinId;
	private float  scale;

	private Vector3f forcedRotation;
	private Vector3f offset;

	public SpriteComponent() {
	}

	public SpriteComponent(String skinId, float scale, Vector3f forcedRotation, Vector3f offset) {
		this.skinId = skinId;
		this.scale = scale;
		this.forcedRotation = forcedRotation;
		this.offset = offset;
	}

	@Override
	protected void initSystems() {
		SpriteSystem spriteSystem = getEntity().getMap().getSystem(SpriteSystem.class);
		DeferredContainer deferredContainer = spriteSystem.getDeferredContainer(skinId);

		PositionComponent position = getEntity().getComponent(PositionComponent.class);

		renderProperties = new RenderProperties3f(position.getRotation(), 0, 0, position.getX(), position.getY() * CoreMap.Y_FACTOR, position.getZ());
		renderProperties.setScale(position.getScale() * scale, position.getScale() * scale, position.getScale() * scale);
		renderable = new DeferredRenderable(deferredContainer, renderProperties);

		spriteSystem.registerEntity(this);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) {
		skinId = data.getStringUTF8("skinId");
		scale = data.getFloat("scale");

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
		if (offset == null) {
			renderProperties.setXYZ(newX, newY * CoreMap.Y_FACTOR, newZ);
		} else {
			renderProperties.setXYZ(newX + offset.getX() * scale, newY + offset.getY() * CoreMap.Y_FACTOR * scale, newZ + offset.getZ() * scale);
		}
		renderProperties.setScale(newScale * scale, newScale * scale, newScale * scale);
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
				scale,
				forcedRotation == null ? null : forcedRotation.clone(),
				offset == null ? null : offset.clone()
		);
	}

}
