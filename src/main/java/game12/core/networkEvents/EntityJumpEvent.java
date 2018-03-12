package game12.core.networkEvents;

import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityJumpEvent extends NetworkEvent {

	public static final byte JUMP   = 0;
	public static final byte IMPACT = 1;

	private byte eventType;
	private int  entity;

	public EntityJumpEvent() {
	}

	public EntityJumpEvent(byte eventType, int entity) {
		this.eventType = eventType;
		this.entity = entity;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		eventType = in.readByte();
		entity = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeByte(eventType);
		out.writeInt(entity);
	}

	public byte getEventType() { return eventType; }

	public int getEntity()     { return entity; }

}
