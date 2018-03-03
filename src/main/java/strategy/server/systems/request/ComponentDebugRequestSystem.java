package strategy.server.systems.request;

import de.nerogar.noise.network.Packet;
import strategy.Strategy;
import strategy.core.ComponentsDebug;
import strategy.core.event.UpdateEvent;
import strategy.core.map.Entity;
import strategy.core.network.StrategyPacketInfo;
import strategy.core.request.ComponentDebugPacket;
import strategy.core.systems.OnUpdateSystem;
import strategy.server.map.ServerMap;

public class ComponentDebugRequestSystem extends OnUpdateSystem {

	private ServerMap map;

	public ComponentDebugRequestSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		if (!Strategy.DEBUG) return;

		for (Packet p : getNetworkAdapter().getPackets(StrategyPacketInfo.DEBUG_SCREEN_CAHNNEL)) {
			if (p instanceof ComponentDebugPacket) {
				componentDebug((ComponentDebugPacket) p);
			}
		}
	}

	private void componentDebug(ComponentDebugPacket packet) {
		Entity entity = map.getEntity(packet.getId());
		if (entity == null) return;

		ComponentDebugPacket response = new ComponentDebugPacket(packet.getId(), ComponentsDebug.generateComponentDebugString(entity.getComponents()));
		getNetworkAdapter().send(response);
	}

}
