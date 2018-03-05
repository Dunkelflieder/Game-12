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

	private static final int MAX_ROOMS     = 1024;
	private static final int MIN_ROOM_SIZE = 20;

	public static final int DOOR = -1;
	public static final int VOID = 0;

	private int   width;
	private int   height;
	private int[] rooms;

	private boolean[] lockedRooms = new boolean[MAX_ROOMS];
	private int[]     cellCount   = new int[MAX_ROOMS];

	public MapSystem(int width, int height) {
		this.width = width;
		this.height = height;
		this.rooms = new int[width * height];

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

		if (!isRoomLocked(oldRoom)) {
			if (checkSpace(x, y, roomId)) {
				rooms[y * width + x] = roomId;
				if (roomId > VOID) cellCount[roomId]++;
				if (oldRoom > VOID) cellCount[oldRoom]--;
				getEventManager().trigger(new MapChangeEvent(x, y));
				callSyncFunction(new UpdateSyncParameter(x, y, roomId));
			}
		}
	}

	private boolean checkSpace(int x, int y, int roomId) {
		if (roomId > VOID && cellCount[roomId] > 0) {
			if (get(x - 1, y) != roomId && get(x + 1, y) != roomId && get(x, y - 1) != roomId && get(x, y + 1) != roomId) return false;
		}

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

	public boolean isRoomLocked(int roomId) {
		if (roomId >= 0) {
			return lockedRooms[roomId];
		} else {
			return false;
		}
	}

	public void lockRoom(int roomId) {
		lockedRooms[roomId] = true;
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

	public boolean checkRoom(int roomId) {
		return cellCount[roomId] >= MIN_ROOM_SIZE;
	}

}
