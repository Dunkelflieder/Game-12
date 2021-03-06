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
import java.util.HashMap;
import java.util.Map;

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

	public static class UpdateTileSyncParameter extends SystemSyncParameter {

		private int x;
		private int y;
		private int tile;

		public UpdateTileSyncParameter() {
		}

		public UpdateTileSyncParameter(int x, int y, int tile) {
			this.x = x;
			this.y = y;
			this.tile = tile;
		}

		@Override
		public void fromStream(DataInputStream in) throws IOException {
			super.fromStream(in);

			x = in.readInt();
			y = in.readInt();
			tile = in.readInt();
		}

		@Override
		public void toStream(DataOutputStream out) throws IOException {
			super.toStream(out);

			out.writeInt(x);
			out.writeInt(y);
			out.writeInt(tile);
		}

	}

	public static class LockSyncParameter extends SystemSyncParameter {

		private int roomId;

		public LockSyncParameter() {
		}

		public LockSyncParameter(int roomId) {
			this.roomId = roomId;
		}

		@Override
		public void fromStream(DataInputStream in) throws IOException {
			super.fromStream(in);
			roomId = in.readInt();
		}

		@Override
		public void toStream(DataOutputStream out) throws IOException {
			super.toStream(out);
			out.writeInt(roomId);
		}

	}

	private static final int MAX_ROOMS       = 1024;
	private static final int MIN_ROOM_SIZE   = 20;
	private static final int START_ROOM_SIZE = 3;

	public static final int VOID        = 0;
	public static final int DOOR        = -1;
	public static final int LOCKED_DOOR = -2;

	public static final int TILE_FLOOR = 0;
	public static final int TILE_LAVA  = 1;
	public static final int TILE_ENEMY = 2;

	private int   width;
	private int   height;
	private int[] rooms;
	private int[] tiles;

	private Map<Integer, Room> roomMap;

	public MapSystem(int width, int height) {
		this.width = width;
		this.height = height;
		this.rooms = new int[width * height];
		this.tiles = new int[width * height];
		this.roomMap = new HashMap<>();

		for (int x = 1; x <= START_ROOM_SIZE; x++) {
			for (int y = 1; y <= START_ROOM_SIZE; y++) {
				rooms[y * width + x] = 1;
			}
		}

		getRoom(1).locked = true;

	}

	@Override
	public void init() {
		super.init();

		registerSyncFunction(UpdateSyncParameter.class, this::syncSet);
		registerSyncFunction(UpdateTileSyncParameter.class, this::syncSetTile);
		registerSyncFunction(LockSyncParameter.class, this::syncLock);
	}

	public int getWidth()  { return width; }

	public int getHeight() { return height; }

	public boolean isWalkable(int x, int y, boolean isPlayer) {
		int roomId = get(x, y);
		int tile = getTile(x, y);

		if (roomId == VOID) return false;
		if (roomId == DOOR) return isPlayer;
		if (roomId == LOCKED_DOOR) return false;

		return true;
	}

	public int get(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= width) return VOID;

		return rooms[y * width + x];
	}

	public boolean set(int x, int y, int roomId) {
		if (!checkSide(Side.SERVER)) return false;

		if (x < 0 || x >= width || y < 0 || y >= width) return false;

		int oldRoom = rooms[y * width + x];

		if (oldRoom == roomId) return false;

		if (!isRoomLocked(oldRoom)) {
			boolean valid = false;

			if (roomId > VOID) {
				valid = checkSpaceRoom(x, y, roomId);
			} else if (roomId == DOOR || roomId == LOCKED_DOOR) {
				valid = checkSpaceDoor(x, y, roomId);
			}

			if (valid) {
				rooms[y * width + x] = roomId;
				if (roomId > VOID) getRoom(roomId).cellCount++;
				if (oldRoom > VOID) getRoom(oldRoom).cellCount--;
				if (roomId == DOOR) {
					if (get(x - 1, y) > VOID) getRoom(get(x - 1, y)).hasDoor = true;
					if (get(x + 1, y) > VOID) getRoom(get(x + 1, y)).hasDoor = true;
					if (get(x, y - 1) > VOID) getRoom(get(x, y - 1)).hasDoor = true;
					if (get(x, y + 1) > VOID) getRoom(get(x, y + 1)).hasDoor = true;
				}
				getEventManager().trigger(new MapChangeEvent(x, y, oldRoom, roomId));
				callSyncFunction(new UpdateSyncParameter(x, y, roomId));
				return true;
			}
		}

		return false;
	}

	public int getTile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= width) return VOID;

		return tiles[y * width + x];
	}

	public boolean setTile(int x, int y, int tileId) {
		if (!checkSide(Side.SERVER)) return false;

		if (x < 0 || x >= width || y < 0 || y >= width) return false;

		int roomId = rooms[y * width + x];
		int oldTile = tiles[y * width + x];

		if (oldTile == tileId) return false;

		if (!isRoomLocked(roomId)) {
			boolean valid = false;

			if (roomId > VOID) {
				valid = checkSpaceRoom(x, y, roomId);
			} else if (roomId == DOOR || roomId == LOCKED_DOOR) {
				valid = checkSpaceDoor(x, y, roomId);
			}

			// TODO set the tile and synchronize

			if (valid) {
				tiles[y * width + x] = tileId;
				callSyncFunction(new UpdateTileSyncParameter(x, y, tileId));
			}

		}

		return false;
	}

	private boolean checkSpaceRoom(int x, int y, int roomId) {
		if (x <= 0 || x >= width - 1 || y <= 0 || y >= height - 1) return false;

		if (roomId > VOID && getRoom(roomId).cellCount > 0) {
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

	private boolean checkSpaceDoor(int x, int y, int doorId) {
		if (x <= 0 || x >= width - 1 || y <= 0 || y >= height - 1) return false;

		if (get(x, y) == VOID) {
			if (get(x - 1, y) > VOID && get(x + 1, y) > VOID && get(x - 1, y) != get(x + 1, y) && (get(x, y - 1) == VOID && get(x, y + 1) == VOID)) return true;
			if (get(x, y - 1) > VOID && get(x, y + 1) > VOID && get(x, y - 1) != get(x, y + 1) && (get(x - 1, y) == VOID && get(x + 1, y) == VOID)) return true;
			return false;
		}

		return false;
	}

	public boolean isRoomLocked(int roomId) {
		if (roomId >= 0) {
			return getRoom(roomId).locked;
		} else {
			return false;
		}
	}

	public void lockRoom(int roomId) {
		getRoom(roomId).locked = true;

		if (checkSide(Side.SERVER)) {
			callSyncFunction(new LockSyncParameter(roomId));
		}
	}

	private void syncLock(LockSyncParameter parameter) {
		lockRoom(parameter.roomId);
	}

	private void syncSet(UpdateSyncParameter parameter) {
		int oldRoom = rooms[parameter.y * width + parameter.x];
		rooms[parameter.y * width + parameter.x] = parameter.roomId;
		getEventManager().trigger(new MapChangeEvent(parameter.x, parameter.y, oldRoom, parameter.roomId));
	}

	private void syncSetTile(UpdateTileSyncParameter parameter) {
		int oldTile = tiles[parameter.y * width + parameter.x];
		tiles[parameter.y * width + parameter.x] = parameter.tile;
		//getEventManager().trigger(new MapChangeEvent(parameter.x, parameter.y, oldTile, parameter.tile));
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
		return getRoom(roomId).cellCount >= MIN_ROOM_SIZE
				&& getRoom(roomId).hasDoor;
	}

	private Room getRoom(int id) {
		if (id < VOID) return null;
		return roomMap.computeIfAbsent(id, i -> new Room());
	}

	private class Room {

		private boolean locked;
		private int     cellCount;
		private boolean hasDoor;
	}

}
