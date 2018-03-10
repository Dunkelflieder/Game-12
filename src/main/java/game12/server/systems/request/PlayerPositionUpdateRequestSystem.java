package game12.server.systems.request;

import game12.core.request.PlayerPositionUpdateRequestPacket;
import game12.core.systems.PlayerSystem;
import game12.server.systems.RequestSystem;

public class PlayerPositionUpdateRequestSystem extends RequestSystem<PlayerPositionUpdateRequestPacket> {

	private PlayerSystem playerSystem;

	public PlayerPositionUpdateRequestSystem() {
		super(PlayerPositionUpdateRequestPacket.class);
	}

	@Override
	public void init() {
		super.init();
		playerSystem = getContainer().getSystem(PlayerSystem.class);
	}

	@Override
	protected void requestFunction(PlayerPositionUpdateRequestPacket request) {
		playerSystem.setPlayerPosition(request.pos);
	}
}
