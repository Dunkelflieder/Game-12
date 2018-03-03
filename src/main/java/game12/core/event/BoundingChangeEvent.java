package game12.core.event;

import de.nerogar.noise.event.Event;
import game12.core.components.BoundingComponent;

public class BoundingChangeEvent implements Event {

	private BoundingComponent boundingComponent;

	public BoundingChangeEvent(BoundingComponent boundingComponent) {
		this.boundingComponent = boundingComponent;
	}

	public BoundingComponent getBoundingComponent() {
		return boundingComponent;
	}

}
