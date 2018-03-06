package game12.core.event;

import de.nerogar.noise.event.Event;

public class MapChangeEvent implements Event {

	private final int x;
	private final int y;
	private final int oldRoom;
	private final int newRoom;

	public MapChangeEvent(int x, int y, int oldRoom, int newRoom) {
		this.x = x;
		this.y = y;
		this.oldRoom = oldRoom;
		this.newRoom = newRoom;
	}

	public int getX()       { return x; }

	public int getY()       { return y; }

	public int getOldRoom() { return oldRoom; }

	public int getNewRoom() { return newRoom; }

}
