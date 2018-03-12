package game12.core.networkEvents;

import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HealthChangedEvent extends NetworkEvent {

	public int entityID;
	public int oldHealth;
	public int oldMaxHealth;
	public int newHealth;
	public int newMaxHealth;

	public HealthChangedEvent() {
	}

	public HealthChangedEvent(int entityID, int oldHealth, int oldMaxHealth, int newHealth, int newMaxHealth) {
		this.entityID = entityID;
		this.oldHealth = oldHealth;
		this.oldMaxHealth = oldMaxHealth;
		this.newHealth = newHealth;
		this.newMaxHealth = newMaxHealth;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		entityID = in.readInt();
		oldHealth = in.readInt();
		oldMaxHealth = in.readInt();
		newHealth = in.readInt();
		newMaxHealth = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(entityID);
		out.writeInt(oldHealth);
		out.writeInt(oldMaxHealth);
		out.writeInt(newHealth);
		out.writeInt(newMaxHealth);
	}
}
