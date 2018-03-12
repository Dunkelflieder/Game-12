package game12.core.networkEvents;

import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityDespawnPacket extends NetworkEvent {

	private int id;

	public EntityDespawnPacket() {
	}

	public EntityDespawnPacket(int id) {
		this.id = id;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		id = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(id);
	}

	public int getId() { return id; }

}
