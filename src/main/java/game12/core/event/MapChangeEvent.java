package game12.core.event;

import de.nerogar.noise.event.Event;

public class MapChangeEvent implements Event {

	private final int x;
	private final int y;

	public MapChangeEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() { return x; }

	public int getY() { return y; }
}
