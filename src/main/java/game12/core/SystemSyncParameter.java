package game12.core;

import de.nerogar.noise.network.Packet;
import game12.core.network.StrategyPacketInfo;

public abstract class SystemSyncParameter extends Packet {

	private short systemId;

	public void setSystemId(short systemId) {
		this.systemId = systemId;
	}

	public short getSystemId() {
		return systemId;
	}

	@Override
	public int getChannel() {
		return StrategyPacketInfo.SYSTEMS_PACKET_CHANNEL;
	}

}
