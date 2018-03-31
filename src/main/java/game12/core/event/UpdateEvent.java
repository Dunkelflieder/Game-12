package game12.core.event;

import de.nerogar.noise.event.Event;

public class UpdateEvent implements Event {

	private float delta;

	public UpdateEvent(float delta) {
		this.delta = delta;
	}

	public float getDelta() {
		return delta;
	}

	@Override
	public String toString() {
		return "UpdateEvent{" +
				"delta=" + delta +
				'}';
	}
}
