package game12.core.request;

import de.nerogar.noise.util.Vector3f;
import game12.core.network.FactionRequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerPositionUpdateRequest extends FactionRequestPacket {

	public Vector3f pos;

	public static PlayerPositionUpdateRequest of(Vector3f pos) {
		PlayerPositionUpdateRequest request = new PlayerPositionUpdateRequest();
		request.pos = pos;
		return request;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		pos = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeFloat(pos.getX());
		out.writeFloat(pos.getY());
		out.writeFloat(pos.getZ());
	}
}
