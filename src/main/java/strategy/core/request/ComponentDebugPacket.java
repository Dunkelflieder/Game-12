package strategy.core.request;

import de.nerogar.noise.network.Packet;
import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;
import strategy.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ComponentDebugPacket extends Packet {

	private int    id;
	private String debugString;

	public ComponentDebugPacket() {
	}

	public ComponentDebugPacket(int id) {
		this.id = id;
	}

	public ComponentDebugPacket(int id, String debugString) {
		this.id = id;
		this.debugString = debugString;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		NDSDataInputStream ndsIn = new NDSDataInputStream(in);

		id = ndsIn.readInt();
		boolean hasDebugString = ndsIn.readBoolean();
		if (hasDebugString) {
			debugString = ndsIn.readUTF8String(false, true);
		} else {
			debugString = null;
		}
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		NDSDataOutputStream ndsOut = new NDSDataOutputStream(out);

		ndsOut.writeInt(id);
		if (debugString != null) {
			ndsOut.writeBoolean(true);
			ndsOut.writeUTF8String(debugString, false, true);
		} else {
			ndsOut.writeBoolean(false);
		}
	}

	public int getId()             { return id; }

	public String getDebugString() { return debugString; }

	@Override
	public int getChannel() {
		return StrategyPacketInfo.DEBUG_SCREEN_CAHNNEL;
	}

}
