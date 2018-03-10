package game12.client.event;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.util.Vector3f;

public class DoorCloseEvent implements Event {
	public Vector3f position;

	public DoorCloseEvent(Vector3f position) {

		this.position = position;
	}
}
