package game12.core.network;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.network.Packet;
import game12.core.Faction;

public abstract class FactionRequestPacket extends Packet implements Event {

	private Faction faction;

	public void setFaction(Faction faction) {
		this.faction = faction;
	}

	public Faction getFaction() {
		return faction;
	}

	@Override
	public int getChannel() {
		return StrategyPacketInfo.REQUEST_PACKET_CHANNEL;
	}

}
