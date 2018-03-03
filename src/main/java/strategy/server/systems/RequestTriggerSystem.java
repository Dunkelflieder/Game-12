package strategy.server.systems;

import de.nerogar.noise.network.Packet;
import strategy.core.Faction;
import strategy.core.event.UpdateEvent;
import strategy.core.network.FactionRequestPacket;
import strategy.core.network.StrategyPacketInfo;
import strategy.core.systems.OnUpdateSystem;

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
