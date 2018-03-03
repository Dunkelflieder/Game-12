package game12.server.event;

import de.nerogar.noise.event.Event;
import game12.core.components.SynchronizedComponent;

public class ComponentUpdateEvent implements Event {

	private SynchronizedComponent component;

	public ComponentUpdateEvent(SynchronizedComponent component) {
		this.component = component;
	}

	public SynchronizedComponent getComponent() {
		return component;
	}

}
