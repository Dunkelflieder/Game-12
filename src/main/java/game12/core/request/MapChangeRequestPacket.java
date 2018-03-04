package game12.core.request;

import game12.core.network.FactionRequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MapChangeRequestPacket extends FactionRequestPacket {

	private int x;
	private int y;

	private int roomId;

	public MapChangeRequestPacket() {
	}

	public MapChangeRequestPacket(int x, int y, int roomId) {
		this.x = x;
		this.y = y;
		this.roomId = roomId;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readInt();
		roomId = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(roomId);
	}

	public int getX()      { return x; }

	public int getY()      { return y; }

	public int getRoomId() { return roomId; }

}
