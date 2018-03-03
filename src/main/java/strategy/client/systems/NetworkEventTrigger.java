package strategy.client.systems;

import de.nerogar.noise.network.Packet;
import strategy.core.event.UpdateEvent;
import strategy.core.network.NetworkEvent;
import strategy.core.network.StrategyPacketInfo;
import strategy.core.systems.OnUpdateSystem;

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
