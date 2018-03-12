package game12.core.request;

import game12.core.network.FactionRequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityPlaceRequestPacket extends FactionRequestPacket {

	private int x;
	private int y;

	private short blueprintId;

	private int cost;

	public EntityPlaceRequestPacket() {
	}

	public EntityPlaceRequestPacket(int x, int y, short blueprintId, int cost) {
		this.x = x;
		this.y = y;
		this.blueprintId = blueprintId;
		this.cost = cost;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readInt();
		blueprintId = in.readShort();
		cost = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeShort(blueprintId);
		out.writeInt(cost);
	}

	public int getX()             { return x; }

	public int getY()             { return y; }

	public short getBlueprintId() { return blueprintId; }

	public int getCost()          { return cost; }

}
