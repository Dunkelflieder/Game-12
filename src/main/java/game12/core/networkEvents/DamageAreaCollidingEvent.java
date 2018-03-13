package game12.core.networkEvents;

import game12.core.misc.DamageType;
import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DamageAreaCollidingEvent extends NetworkEvent {

	public int        damage;
	public DamageType damageType;
	public int        entityID;
	public int        damageEntityID;

	public DamageAreaCollidingEvent() {
	}

	public DamageAreaCollidingEvent(int damage, DamageType damageType, int entityID, int damageEntityID) {
		this.damage = damage;
		this.damageType = damageType;
		this.entityID = entityID;
		this.damageEntityID = damageEntityID;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		damage = in.readInt();
		entityID = in.readInt();
		damageEntityID = in.readInt();
		damageType = DamageType.fromId(in.readInt());
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(damage);
		out.writeInt(entityID);
		out.writeInt(damageEntityID);
		out.writeInt(damageType.id);
	}
}
