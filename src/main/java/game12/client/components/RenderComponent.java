package game12.client.components;

import de.nerogar.noise.render.RenderProperties3f;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderable;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import de.nerogar.noise.serialization.NDSNodeObject;
import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.client.map.ClientMap;
import game12.client.systems.EntityRenderResourcesSystem;
import game12.client.systems.UpdateRenderablesSystem;
import game12.core.components.PositionComponent;
import game12.core.map.Component;
import game12.core.map.CoreMap;
import game12.core.systems.GameObjectsSystem;

import java.util.Random;

@ComponentInfo(name = "render", side = ComponentSide.CLIENT)
public class RenderComponent extends Component {

	private DeferredRenderer renderer;

	private RenderProperties3f renderProperties;
	private DeferredRenderable renderable;

	private String[] skinIDs;

	public RenderComponent() {
	}

	public RenderComponent(String[] skinIDs) {
		this.skinIDs = skinIDs;
	}

	@Override
	protected void initSystems() {
		ClientMap clientMap = (ClientMap) getEntity().getMap();

		// TODO this produces very similar results for similar IDs
		Random random = new Random(getEntity().getID());

		DeferredContainer deferredContainer = clientMap.getGameSystem(EntityRenderResourcesSystem.class).getDeferredContainer(skinIDs[random.nextInt(skinIDs.length)]);

		PositionComponent position = getEntity().getComponent(PositionComponent.class);

		renderProperties = new RenderProperties3f(position.getRotation(), 0, 0, position.getX(), position.getY() * CoreMap.Y_FACTOR, position.getZ());
		renderProperties.setScale(position.getScale(), position.getScale(), position.getScale());
		renderable = new DeferredRenderable(deferredContainer, renderProperties);

		getEntity().getMap().getSystem(UpdateRenderablesSystem.class).registerEntity(this);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) {
		skinIDs = data.getStringUTF8Array("skinIDs");
	}

	public DeferredRenderable getRenderable() { return renderable; }

	public void updatePosition(float newX, float newY, float newZ, float newRotation, float newScale) {
		renderProperties.setXYZ(newX, newY * CoreMap.Y_FACTOR, newZ);
		renderProperties.setYaw(newRotation);
		renderProperties.setScale(newScale, newScale, newScale);
	}

	public String[] getSkinIDs() {
		return skinIDs;
	}

	@Override
	protected void cleanup() {
		getEntity().getMap().getSystem(UpdateRenderablesSystem.class).unregisterEntity(this);
	}

	@Override
	public Component clone() {
		return new RenderComponent(skinIDs);
	}

}
