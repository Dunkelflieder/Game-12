package game12.core.networkEvents;

import de.nerogar.noise.util.Vector3f;
import game12.core.misc.DamageType;
import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DamageAreaCollidingEvent extends NetworkEvent {

	public int        damage;
	public DamageType damageType;
	public int        entityID;
	public Vector3f   damagePosition;

	public DamageAreaCollidingEvent() {
	}

	public DamageAreaCollidingEvent(int damage, DamageType damageType, int entityID, Vector3f damagePosition) {
		this.damage = damage;
		this.damageType = damageType;
		this.entityID = entityID;
		this.damagePosition = damagePosition;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		damage = in.readInt();
		entityID = in.readInt();
		damagePosition = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		damageType = DamageType.fromId(in.readInt());
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(damage);
		out.writeInt(entityID);
		out.writeFloat(damagePosition.getX());
		out.writeFloat(damagePosition.getY());
		out.writeFloat(damagePosition.getZ());
		out.writeInt(damageType.id);
	}
}
