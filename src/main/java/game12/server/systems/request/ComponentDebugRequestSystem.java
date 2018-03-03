package game12.server.systems.request;

import de.nerogar.noise.network.Packet;
import game12.Game12;
import game12.core.ComponentsDebug;
import game12.core.event.UpdateEvent;
import game12.core.map.Entity;
import game12.core.network.StrategyPacketInfo;
import game12.core.request.ComponentDebugPacket;
import game12.core.systems.OnUpdateSystem;
import game12.server.map.ServerMap;

public class ComponentDebugRequestSystem extends OnUpdateSystem {

	private ServerMap map;

	public ComponentDebugRequestSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		if (!Game12.DEBUG) return;

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
