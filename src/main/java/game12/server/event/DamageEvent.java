package game12.server.event;

import game12.core.network.NetworkEvent;
import game12.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DamageEvent extends NetworkEvent {

	public int entityID;
	public int oldHealth;
	public int newHealth;

	public DamageEvent() {
	}

	public DamageEvent(int entityID, int oldHealth, int newHealth) {
		this.entityID = entityID;
		this.oldHealth = oldHealth;
		this.newHealth = newHealth;
	}

	@Override
	public int getChannel() {
		return StrategyPacketInfo.EVENT_PACKET_CHANNEL;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		entityID = in.readInt();
		oldHealth = in.readInt();
		newHealth = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(entityID);
		out.writeInt(oldHealth);
		out.writeInt(newHealth);
	}
}
