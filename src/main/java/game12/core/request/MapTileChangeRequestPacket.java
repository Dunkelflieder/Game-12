package game12.core.request;

import game12.core.network.FactionRequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MapTileChangeRequestPacket extends FactionRequestPacket {

	private int x;
	private int y;

	private int tile;

	public MapTileChangeRequestPacket() {
	}

	public MapTileChangeRequestPacket(int x, int y, int tile) {
		this.x = x;
		this.y = y;
		this.tile = tile;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readInt();
		tile = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(tile);
	}

	public int getX()    { return x; }

	public int getY()    { return y; }

	public int getTile() { return tile; }

}
