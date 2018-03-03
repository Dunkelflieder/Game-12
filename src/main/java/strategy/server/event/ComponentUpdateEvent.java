package strategy.server.event;

import de.nerogar.noise.event.Event;
import strategy.core.components.SynchronizedComponent;

public class ComponentUpdateEvent implements Event {

	private SynchronizedComponent component;

	public ComponentUpdateEvent(SynchronizedComponent component) {
		this.component = component;
	}

	public SynchronizedComponent getComponent() {
		return component;
	}

}
