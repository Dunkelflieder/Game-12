package game12.server.systems.request;

import game12.core.request.MapTileChangeRequestPacket;
import game12.core.systems.MapSystem;
import game12.server.map.ServerMap;
import game12.server.systems.RequestSystem;

public class MapTileChangeRequestSystem extends RequestSystem<MapTileChangeRequestPacket> {

	private ServerMap map;

	private MapSystem mapSystem;

	public MapTileChangeRequestSystem(ServerMap map) {
		super(MapTileChangeRequestPacket.class);

		this.map = map;
	}

	@Override
	public void init() {
		super.init();

		this.mapSystem = map.getSystem(MapSystem.class);
	}

	@Override
	protected void requestFunction(MapTileChangeRequestPacket request) {
		mapSystem.setTile(request.getX(), request.getY(), request.getTile());
	}

}
