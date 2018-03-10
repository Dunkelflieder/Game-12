package game12.server.systems.request;

import game12.core.EntityFactorySystem;
import game12.core.request.EntityPlaceRequestPacket;
import game12.core.systems.MapSystem;
import game12.server.map.ServerMap;
import game12.server.systems.RequestSystem;

public class EntityPlaceRequestSystem extends RequestSystem<EntityPlaceRequestPacket> {

	private ServerMap map;

	private MapSystem mapSystem;

	public EntityPlaceRequestSystem(ServerMap map) {
		super(EntityPlaceRequestPacket.class);

		this.map = map;
	}

	@Override
	public void init() {
		super.init();

		this.mapSystem = map.getSystem(MapSystem.class);
	}

	@Override
	protected void requestFunction(EntityPlaceRequestPacket request) {
		map.getSystem(EntityFactorySystem.class).createEntity(request.getBlueprintId(), request.getX() + 0.5f, 0, request.getY() + 0.5f);
	}

}
