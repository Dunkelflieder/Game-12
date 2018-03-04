package game12.server.systems.request;

import game12.core.components.PlayerComponent;
import game12.core.components.PositionComponent;
import game12.core.request.PlayerPositionUpdateRequest;
import game12.server.map.ServerMap;
import game12.server.systems.RequestSystem;

public class PlayerPositionUpdateRequestSystem extends RequestSystem<PlayerPositionUpdateRequest> {

	private ServerMap map;

	public PlayerPositionUpdateRequestSystem(ServerMap map) {
		super(PlayerPositionUpdateRequest.class);
		this.map = map;
	}

	@Override
	protected void requestFunction(PlayerPositionUpdateRequest request) {
		for (PlayerComponent playerComponent : map.getEntityList().getComponents(PlayerComponent.class)) {
			PositionComponent positionComponent = playerComponent.getEntity().getComponent(PositionComponent.class);
			positionComponent.setPosition(request.pos.getX(), request.pos.getY(), request.pos.getZ());
		}
	}
}
