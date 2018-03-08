package game12.server.event;

import game12.core.network.NetworkEvent;
import game12.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DamageEvent extends NetworkEvent {

	public int   entityID;
	public float oldHealth;
	public float newHealth;

	public DamageEvent() {
	}

	public DamageEvent(int entityID, float oldHealth, float newHealth) {
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
		oldHealth = in.readFloat();
		newHealth = in.readFloat();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(entityID);
		out.writeFloat(oldHealth);
		out.writeFloat(newHealth);
	}
}
