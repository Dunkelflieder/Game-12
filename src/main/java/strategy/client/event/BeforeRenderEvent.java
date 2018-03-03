package strategy.client.event;

import de.nerogar.noise.event.Event;

public class BeforeRenderEvent implements Event {

	private float delta;

	public BeforeRenderEvent(float delta) {
		this.delta = delta;
	}

	public float getDelta() {
		return delta;
	}

}
