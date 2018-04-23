package game12.server.systems.request;

import game12.core.EntityFactorySystem;
import game12.core.request.EntityPlaceRequestPacket;
import game12.core.systems.MapSystem;
import game12.server.map.ServerMap;
import game12.server.systems.RequestSystem;

import java.util.HashMap;
import java.util.Map;

public class EntityPlaceRequestSystem extends RequestSystem<EntityPlaceRequestPacket> {

	private static final int       MAX_ROOM_POINTS = 30;
	private              ServerMap map;

	private MapSystem mapSystem;

	private Map<Integer, Integer> roomPoints;

	public EntityPlaceRequestSystem(ServerMap map) {
		super(EntityPlaceRequestPacket.class);

		this.map = map;

		roomPoints = new HashMap<>();
	}

	@Override
	public void init() {
		super.init();

		this.mapSystem = map.getSystem(MapSystem.class);
	}

	private int getRoomPoints(int roomId) {
		return roomPoints.computeIfAbsent(roomId, id -> MAX_ROOM_POINTS);
	}

	private void setRoomPoints(int roomId, int points) {
		roomPoints.put(roomId, points);
	}

	@Override
	protected void requestFunction(EntityPlaceRequestPacket request) {
		if (!mapSystem.isWalkable(request.getX(), request.getY(), false)) return;

		int roomId = mapSystem.get(request.getX(), request.getY());
		int roomPoints = getRoomPoints(roomId);
		if (roomPoints < request.getCost()) return;

		setRoomPoints(roomId, roomPoints - request.getCost());
		if (getRoomPoints(roomId) <= 0) mapSystem.lockRoom(roomId);

		map.getSystem(EntityFactorySystem.class).createEntity(request.getBlueprintId(), request.getX() + 0.5f, 0, request.getY() + 0.5f);
	}

}
