package strategy.core.networkEvents;

import strategy.core.network.NetworkEvent;
import strategy.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityDespawnPacket extends NetworkEvent {

	private int id;

	public EntityDespawnPacket() {
	}

	public EntityDespawnPacket(int id) {
		this.id = id;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		id = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(id);
	}

	public int getId() { return id; }

	@Override
	public int getChannel() {
		return StrategyPacketInfo.EVENT_PACKET_CHANNEL;
	}

}
