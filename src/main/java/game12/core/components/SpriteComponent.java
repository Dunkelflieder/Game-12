package game12.client.components;

import de.nerogar.noise.render.RenderProperties3f;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderable;
import de.nerogar.noise.serialization.NDSNodeObject;
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

	public SpriteComponent() {
	}

	public SpriteComponent(String skinId) {
		this.skinId = skinId;
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
	}

	public DeferredRenderable getRenderable() { return renderable; }

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
		return new SpriteComponent(skinId);
	}

}
