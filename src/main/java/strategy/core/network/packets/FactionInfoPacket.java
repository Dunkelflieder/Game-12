package strategy.core.network.packets;

import de.nerogar.noise.network.Packet;
import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;
import strategy.core.Faction;
import strategy.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FactionInfoPacket extends Packet {

	private int[] factionIDs;
	private int[]  factionColors;
	private byte   ownFaction;

	public FactionInfoPacket() {
	}

	public FactionInfoPacket(Faction[] factions, byte ownFaction) {
		factionIDs = new int[factions.length];
		factionColors = new int[factions.length];
		for (int i = 0; i < factions.length; i++) {
			factionIDs[i] = factions[i].getID();
			factionColors[i] = factions[i].getColor().getARGB();
		}
		this.ownFaction = ownFaction;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		NDSDataInputStream ndsIn = new NDSDataInputStream(in);

		factionIDs = ndsIn.readUnsignedShortArray();
		factionColors = ndsIn.readIntArray();
		ownFaction = ndsIn.readByte();

	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		NDSDataOutputStream ndsOut = new NDSDataOutputStream(out);

		ndsOut.writeUnsignedShortArray(factionIDs);
		ndsOut.writeIntArray(factionColors);
		ndsOut.writeByte(ownFaction);

	}

	public int[] getFactionIDs()   { return factionIDs; }

	public int[] getFactionColors() { return factionColors; }

	public byte getOwnFaction()     { return ownFaction; }

	@Override
	public int getChannel() {
		return StrategyPacketInfo.CONTROL_PACKET_CHANNEL;
	}

}
