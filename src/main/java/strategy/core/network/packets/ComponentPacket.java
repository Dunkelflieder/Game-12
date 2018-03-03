package strategy.core.network.packets;

import strategy.core.Components;
import strategy.core.components.SynchronizedComponent;
import strategy.core.network.NetworkEvent;
import strategy.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ComponentPacket extends NetworkEvent {

	private int   id;
	private short componentID;

	private SynchronizedComponent component;
	private DataInputStream       input;

	public ComponentPacket() {
	}

	public ComponentPacket(SynchronizedComponent component) {
		this.component = component;

		id = component.getEntity().getID();
		componentID = Components.getIDFromComponent(component.getClass());
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		id = in.readInt();
		componentID = in.readShort();

		this.input = in;
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(id);
		out.writeShort(componentID);

		component.toStream(out);
	}

	public int getId()                { return id; }

	public short getComponentID()     { return componentID; }

	public DataInputStream getInput() { return input; }

	@Override
	public int getChannel() {
		return StrategyPacketInfo.EVENT_PACKET_CHANNEL;
	}

}
