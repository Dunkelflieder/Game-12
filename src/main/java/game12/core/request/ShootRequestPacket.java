package game12.core.request;

import de.nerogar.noise.util.Vector3f;
import game12.core.network.FactionRequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ShootRequestPacket extends FactionRequestPacket {

	public static final byte TYPE_SHOTGUN      = 1;
	public static final byte TYPE_FLAMETHROWER = 2;

	public byte     shotType;
	public Vector3f start;
	public Vector3f direction;

	public ShootRequestPacket() {
	}

	public ShootRequestPacket(byte shotType, Vector3f start, Vector3f direction) {
		this.shotType = shotType;
		this.start = start;
		this.direction = direction;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		shotType = in.readByte();
		start = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		direction = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeByte(shotType);
		out.writeFloat(start.getX());
		out.writeFloat(start.getY());
		out.writeFloat(start.getZ());
		out.writeFloat(direction.getX());
		out.writeFloat(direction.getY());
		out.writeFloat(direction.getZ());
	}
}
