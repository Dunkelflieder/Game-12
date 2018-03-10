package game12.core.request;

import game12.core.network.FactionRequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityPlaceRequestPacket extends FactionRequestPacket {

	private int x;
	private int y;

	private short blueprintId;

	public EntityPlaceRequestPacket() {
	}

	public EntityPlaceRequestPacket(int x, int y, short blueprintId) {
		this.x = x;
		this.y = y;
		this.blueprintId = blueprintId;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readInt();
		blueprintId = in.readShort();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeShort(blueprintId);
	}

	public int getX()             { return x; }

	public int getY()             { return y; }

	public short getBlueprintId() { return blueprintId; }
}
