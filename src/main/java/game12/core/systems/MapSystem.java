package game12.core.systems;

import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;
import game12.core.Side;
import game12.core.SynchronizedSystem;
import game12.core.SystemSyncParameter;
import game12.core.event.MapChangeEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MapSystem extends SynchronizedSystem {

	public static class UpdateSyncParameter extends SystemSyncParameter {

		private int x;
		private int y;
		private int roomId;

		public UpdateSyncParameter() {
		}

		public UpdateSyncParameter(int x, int y, int roomId) {
			this.x = x;
			this.y = y;
			this.roomId = roomId;
		}

		@Override
		public void fromStream(DataInputStream in) throws IOException {
			super.fromStream(in);

			x = in.readInt();
			y = in.readInt();
			roomId = in.readInt();
		}

		@Override
		public void toStream(DataOutputStream out) throws IOException {
			super.toStream(out);

			out.writeInt(x);
			out.writeInt(y);
			out.writeInt(roomId);
		}

	}

	public static final int DOOR = -1;
	public static final int VOID = 0;

	private int          width;
	private int          height;
	private int[]        rooms;
	private Set<Integer> lockedRooms;

	public MapSystem(int width, int height) {
		this.width = width;
		this.height = height;
		this.rooms = new int[width * height];

		lockedRooms = new HashSet<>();

		for (int x = 1; x <= 5; x++) {
			for (int y = 1; y <= 5; y++) {
				rooms[y * width + x] = 1;
			}
		}
		lockRoom(1);

	}

	@Override
	public void init() {
		super.init();

		registerSyncFunction(UpdateSyncParameter.class, this::syncSet);
	}

	public int getWidth()  { return width; }

	public int getHeight() { return height; }

	public int get(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= width) return VOID;

		return rooms[y * width + x];
	}

	public void set(int x, int y, int roomId) {
		if (!checkSide(Side.SERVER)) return;

		if (x < 0 || x >= width || y < 0 || y >= width) return;

		int oldRoom = rooms[y * width + x];

		if (!(lockedRooms.contains(oldRoom))) {
			if (checkSpace(x, y, roomId)) {
				rooms[y * width + x] = roomId;
				getEventManager().trigger(new MapChangeEvent(x, y));
				callSyncFunction(new UpdateSyncParameter(x, y, roomId));
			}
		}
	}

	private boolean checkSpace(int x, int y, int roomId) {
		if (get(x - 1, y) > VOID && get(x - 1, y) != roomId) return false;
		if (get(x - 1, y + 1) > VOID && get(x - 1, y + 1) != roomId) return false;
		if (get(x, y + 1) > VOID && get(x, y + 1) != roomId) return false;
		if (get(x + 1, y + 1) > VOID && get(x + 1, y + 1) != roomId) return false;
		if (get(x + 1, y) > VOID && get(x + 1, y) != roomId) return false;
		if (get(x + 1, y - 1) > VOID && get(x + 1, y - 1) != roomId) return false;
		if (get(x, y - 1) > VOID && get(x, y - 1) != roomId) return false;
		if (get(x - 1, y - 1) > VOID && get(x - 1, y - 1) != roomId) return false;

		return true;
	}

	public void lockRoom(int roomId) {
		lockedRooms.add(roomId);
	}

	private void syncSet(UpdateSyncParameter parameter) {
		rooms[parameter.y * width + parameter.x] = parameter.roomId;
		getEventManager().trigger(new MapChangeEvent(parameter.x, parameter.y));
	}

	@Override
	public void sendNetworkInit(DataOutputStream out) throws IOException {
		NDSDataOutputStream stream = new NDSDataOutputStream(out);
		stream.writeInt(width);
		stream.writeInt(height);
		stream.writeIntArray(rooms);
	}

	@Override
	public void networkInit(DataInputStream in) throws IOException {
		NDSDataInputStream stream = new NDSDataInputStream(in);
		width = stream.readInt();
		height = stream.readInt();
		rooms = stream.readIntArray();
	}
}
