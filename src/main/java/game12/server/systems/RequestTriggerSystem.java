package game12.server.systems;

import de.nerogar.noise.network.Packet;
import game12.core.Faction;
import game12.core.event.UpdateEvent;
import game12.core.network.FactionRequestPacket;
import game12.core.network.StrategyPacketInfo;
import game12.core.systems.OnUpdateSystem;

public class RequestTriggerSystem extends OnUpdateSystem {

	private Faction faction;

	public RequestTriggerSystem(Faction faction) {
		this.faction = faction;
	}

	@Override
	protected void updateListenerFunction(UpdateEvent updateEvent) {
		for (Packet packet : getNetworkAdapter().getPackets(StrategyPacketInfo.REQUEST_PACKET_CHANNEL)) {
			FactionRequestPacket factionRequestPacket = (FactionRequestPacket) packet;
			factionRequestPacket.setFaction(faction);
			getEventManager().trigger(factionRequestPacket);
		}
	}

}
