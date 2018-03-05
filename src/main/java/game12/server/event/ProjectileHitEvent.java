package game12.server.event;

import game12.core.network.NetworkEvent;
import game12.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProjectileHitEvent extends NetworkEvent {

	public int entityID;

	public ProjectileHitEvent() {
	}

	public ProjectileHitEvent(int entityID) {
		this.entityID = entityID;
	}

	@Override
	public int getChannel() {
		return StrategyPacketInfo.EVENT_PACKET_CHANNEL;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		entityID = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(entityID);
	}
}
