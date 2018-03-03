package game12.client.systems;

import de.nerogar.noise.network.Packet;
import game12.core.event.UpdateEvent;
import game12.core.network.NetworkEvent;
import game12.core.network.StrategyPacketInfo;
import game12.core.systems.OnUpdateSystem;

import java.util.List;

public class NetworkEventTrigger extends OnUpdateSystem {

	public NetworkEventTrigger() {
	}

	@Override
	protected void updateListenerFunction(UpdateEvent updateEvent) {
		List<Packet> packets;

		packets = getNetworkAdapter().getPackets(StrategyPacketInfo.EVENT_PACKET_CHANNEL);
		for (Packet packet : packets) {
			NetworkEvent networkEvent = (NetworkEvent) packet;

			getEventManager().trigger(networkEvent);
		}
	}

}
