package game12.client.components;

import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.core.components.PositionComponent;
import game12.core.map.Component;

@ComponentInfo(name = "interpolatePosition", side = ComponentSide.CLIENT)
public class InterpolatePositionComponent extends Component {

	private float time;
	private float progress;

	private float oldX, oldY, oldZ, oldRotation, oldScale;
	private float deltaX, deltaY, deltaZ, deltaRotation, deltaScale;

	private PositionComponent positionComponent;

	@Override
	protected void initSystems() {
		positionComponent = getEntity().getComponent(PositionComponent.class);
	}

	@Override
	protected void init() {
		// set the current movement to finished
		progress = 1;
	}

	public void newMovement(float time, float newX, float newY, float newZ, float newRotation, float newScale) {
		this.time = time;

		this.oldX = positionComponent.getX();
		this.oldY = positionComponent.getY();
		this.oldZ = positionComponent.getZ();
		this.oldRotation = positionComponent.getRotation();
		this.oldScale = positionComponent.getScale();

		this.deltaX = newX - oldX;
		this.deltaY = newY - oldY;
		this.deltaZ = newZ - oldZ;
		this.deltaRotation = newRotation - oldRotation;
		this.deltaScale = newScale - oldScale;

		progress = 0;
	}

	public void update(float delta) {
		if (progress >= 1) return;

		progress += delta / time;
		progress = Math.min(progress, 1);

		positionComponent.setPosition(
				oldX + deltaX * progress,
				oldY + deltaY * progress,
				oldZ + deltaZ * progress,
				oldRotation + deltaRotation * progress,
				oldScale + deltaScale * progress
		                             );

	}

	@Override
	public Component clone() {
		return new InterpolatePositionComponent();
	}

}
