package game12.core.network;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.network.Packet;

public abstract class NetworkEvent extends Packet implements Event{

	@Override
	public int getChannel() {
		return StrategyPacketInfo.EVENT_PACKET_CHANNEL;
	}

}
