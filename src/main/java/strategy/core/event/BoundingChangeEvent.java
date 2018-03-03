package strategy.core.event;

import de.nerogar.noise.event.Event;
import strategy.core.components.BoundingComponent;

public class BoundingChangeEvent implements Event {

	private BoundingComponent boundingComponent;

	public BoundingChangeEvent(BoundingComponent boundingComponent) {
		this.boundingComponent = boundingComponent;
	}

	public BoundingComponent getBoundingComponent() {
		return boundingComponent;
	}

}
