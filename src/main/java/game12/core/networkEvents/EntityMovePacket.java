package game12.core.networkEvents;

import game12.core.network.NetworkEvent;
import game12.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityMovePacket extends NetworkEvent {

	private int entityID;

	private float time;
	private float x, y, z, rotation, scale;

	public EntityMovePacket() {
	}

	public EntityMovePacket(int entityID, float time, float x, float y, float z, float rotation, float scale) {
		this.entityID = entityID;

		this.time = time;

		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.scale = scale;
	}

	public int getEntityID()   { return entityID; }

	public float getTime()     { return time; }

	public float getX()        { return x; }

	public float getY()        { return y; }

	public float getZ()        { return z; }

	public float getRotation() { return rotation; }

	public float getScale()    { return scale; }

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		entityID = in.readInt();

		time = in.readFloat();

		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		rotation = in.readFloat();
		scale = in.readFloat();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(entityID);

		out.writeFloat(time);

		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
		out.writeFloat(rotation);
		out.writeFloat(scale);
	}

	@Override
	public int getChannel() {
		return StrategyPacketInfo.EVENT_PACKET_CHANNEL;
	}

}
