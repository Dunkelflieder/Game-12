package game12.client.components;

import de.nerogar.noise.render.deferredRenderer.Light;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Vector3f;
import game12.client.systems.UpdateLightsSystem;
import game12.core.components.PositionComponent;
import game12.core.map.Component;
import game12.core.map.CoreMap;
import game12.core.systems.GameObjectsSystem;

public class LightComponent extends Component {

	private Light    light;
	private Vector3f localPosition;

	public LightComponent() {
	}

	public LightComponent(Vector3f position, Color color, float reach, float intensity) {
		localPosition = position.clone();
		this.light = new Light(localPosition.clone(), color, reach, intensity);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) {
		float[] positionArray = data.getFloatArray("position");
		float[] colorArray = data.getFloatArray("color");
		localPosition = new Vector3f(positionArray[0], positionArray[1], positionArray[2]);
		Color color = new Color(colorArray[0], colorArray[1], colorArray[2], 1);

		light = new Light(new Vector3f(), color, data.getFloat("reach"), data.getFloat("intensity"));
	}

	@Override
	protected void initSystems() {
		getEntity().getMap().getSystem(UpdateLightsSystem.class).registerEntity(this);
	}

	@Override
	protected void init() {
		setPosition();
	}

	public Light getLight() { return light; }

	public void setPosition() {
		PositionComponent positionComponent = getEntity().getComponent(PositionComponent.class);
		light.position.set(positionComponent.getX(), positionComponent.getY() * CoreMap.Y_FACTOR, positionComponent.getZ()).add(localPosition);
	}

	@Override
	protected void cleanup() {
		getEntity().getMap().getSystem(UpdateLightsSystem.class).unregisterEntity(this);
	}

	@Override
	public Component clone() {
		return new LightComponent(localPosition, light.color, light.reach, light.intensity);
	}

}
