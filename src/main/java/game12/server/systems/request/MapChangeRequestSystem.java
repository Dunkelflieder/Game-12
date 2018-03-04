package game12.server.systems.request;

import game12.core.request.MapChangeRequestPacket;
import game12.core.systems.MapSystem;
import game12.server.map.ServerMap;
import game12.server.systems.RequestSystem;

public class MapChangeRequestSystem extends RequestSystem<MapChangeRequestPacket> {

	private ServerMap map;

	private MapSystem mapSystem;

	public MapChangeRequestSystem(ServerMap map) {
		super(MapChangeRequestPacket.class);

		this.map = map;
	}

	@Override
	public void init() {
		super.init();

		this.mapSystem = map.getSystem(MapSystem.class);
	}

	@Override
	protected void requestFunction(MapChangeRequestPacket request) {
		mapSystem.set(request.getX(), request.getY(), request.getRoomId());
	}

}
