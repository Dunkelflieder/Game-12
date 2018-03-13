package game12.core.networkEvents;

import de.nerogar.noise.util.Vector3f;
import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DamageImpactEvent extends NetworkEvent {

	public int      entityID;
	public Vector3f position;
	public Vector3f direction;

	public DamageImpactEvent() {
	}

	public DamageImpactEvent(int entityID, Vector3f position, Vector3f direction) {
		this.entityID = entityID;
		this.position = position;
		this.direction = direction;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		entityID = in.readInt();
		position = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		direction = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(entityID);
		out.writeFloat(position.getX());
		out.writeFloat(position.getY());
		out.writeFloat(position.getZ());
		out.writeFloat(direction.getX());
		out.writeFloat(direction.getY());
		out.writeFloat(direction.getZ());
	}
}
