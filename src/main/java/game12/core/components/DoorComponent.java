package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.client.event.DoorCloseEvent;
import game12.client.event.DoorOpenEvent;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@ComponentInfo(name = "door", side = ComponentSide.CORE)
public class DoorComponent extends SynchronizedComponent {

	private Vector3f position;
	private Vector3f openDirection;

	private int     keyId;
	private boolean isLocked;

	private float targetState;
	private float state;
	private float speed;

	private PositionComponent positionComponent;

	public DoorComponent() {
	}

	public DoorComponent(float speed) {
		this.speed = speed;

		position = new Vector3f();
		openDirection = new Vector3f();
	}

	@Override
	protected void init() {
		this.positionComponent = getEntity().getComponent(PositionComponent.class);
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		this.speed = data.getFloat("speed");
	}

	public void setInitialState(Vector3f position, Vector3f openDirection, boolean isLocked, int keyId) {
		this.position.set(position);
		this.openDirection.set(openDirection);
		this.isLocked = isLocked;
		this.keyId = keyId;

		synchronize();
	}

	public void setTargetState(boolean open) {
		int targetState = open ? 1 : 0;
		if (this.targetState != targetState) {
			this.targetState = targetState;

			synchronize();

			if (open) {
				getEntity().getMap().getEventManager().trigger(new DoorOpenEvent(position));
			} else {
				getEntity().getMap().getEventManager().trigger(new DoorCloseEvent(position));
			}

		}
	}

	public Vector3f getPosition()      { return position; }

	public Vector3f getOpenDirection() { return openDirection; }

	public boolean isLocked()          { return isLocked; }

	public void update(float timeDelta) {
		if (isLocked) return;

		if (targetState != state) {
			float direction = Math.signum(targetState - state);

			state += timeDelta * speed * direction;

			if (Math.signum(targetState - state) != direction) {
				state = targetState;
			}

			Vector3f currentPosition = openDirection.multiplied(state).add(position);
			positionComponent.setPosition(currentPosition.getX(), currentPosition.getY(), currentPosition.getZ());
		}
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		position.setX(in.readFloat());
		position.setY(in.readFloat());
		position.setZ(in.readFloat());

		openDirection.setX(in.readFloat());
		openDirection.setY(in.readFloat());
		openDirection.setZ(in.readFloat());

		targetState = in.readFloat();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeFloat(position.getX());
		out.writeFloat(position.getY());
		out.writeFloat(position.getZ());

		out.writeFloat(openDirection.getX());
		out.writeFloat(openDirection.getY());
		out.writeFloat(openDirection.getZ());

		out.writeFloat(state);
	}

	@Override
	protected void cleanup() {
		super.cleanup();
	}

	@Override
	public Component clone() {
		return new DoorComponent(speed);
	}

}

