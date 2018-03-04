package game12.core;

import de.nerogar.noise.network.Packet;
import game12.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class SystemSyncParameter extends Packet {

	private short systemId;

	public void setSystemId(short systemId) {
		this.systemId = systemId;
	}

	public short getSystemId() {
		return systemId;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		systemId = in.readShort();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeShort(systemId);
	}

	@Override
	public int getChannel() {
		return StrategyPacketInfo.SYSTEMS_PACKET_CHANNEL;
	}

}
