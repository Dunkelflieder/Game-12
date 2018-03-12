package game12.core.networkEvents;

import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntitySpawnPacket extends NetworkEvent {

	private short entityID;
	private int   id;
	private float x;
	private float y;
	private float z;

	public EntitySpawnPacket() {
	}

	public EntitySpawnPacket(short entityID, int id, float x, float y, float z) {
		this.entityID = entityID;
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		entityID = in.readShort();
		id = in.readInt();
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeShort(entityID);
		out.writeInt(id);
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
	}

	public short getEntityID() { return entityID; }

	public int getId()         { return id; }

	public float getX()        { return x; }

	public float getY()        { return y; }

	public float getZ()        { return z; }

}
