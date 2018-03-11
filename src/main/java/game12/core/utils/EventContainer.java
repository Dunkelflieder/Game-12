package game12.core.utils;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.event.EventListener;

import java.util.ArrayList;
import java.util.List;

public class EventContainer<T extends Event> {

	private List<EventListener<T>> handlers = new ArrayList<>();

	public void trigger(T event) {
		for (EventListener<T> handler : handlers) {
			handler.trigger(event);
		}
	}

	public void register(EventListener<T> handler) {
		handlers.add(handler);
	}

	public boolean unregister(EventListener<T> handler) {
		return handlers.remove(handler);
	}
}
