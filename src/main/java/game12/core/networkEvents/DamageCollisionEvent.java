package game12.core.networkEvents;

import de.nerogar.noise.util.Vector3f;
import game12.core.misc.DamageType;
import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DamageCollisionEvent extends NetworkEvent {

	public int        damage;
	public DamageType damageType;
	public int        entityID;
	public Vector3f   position;
	public Vector3f   direction;

	public DamageCollisionEvent() {
	}

	public DamageCollisionEvent(int damage, DamageType damageType, int entityID, Vector3f position, Vector3f direction) {
		this.damage = damage;
		this.damageType = damageType;
		this.entityID = entityID;
		this.position = position;
		this.direction = direction;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		damage = in.readInt();
		entityID = in.readInt();
		position = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		direction = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		damageType = DamageType.valueOf(in.readUTF());
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(damage);
		out.writeInt(entityID);
		out.writeFloat(position.getX());
		out.writeFloat(position.getY());
		out.writeFloat(position.getZ());
		out.writeFloat(direction.getX());
		out.writeFloat(direction.getY());
		out.writeFloat(direction.getZ());
		out.writeUTF(damageType.name());
	}
}
