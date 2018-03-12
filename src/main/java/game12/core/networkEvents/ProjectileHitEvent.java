package game12.core.networkEvents;

import de.nerogar.noise.util.Vector3f;
import game12.core.components.ProjectileComponent;
import game12.core.network.NetworkEvent;
import game12.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProjectileHitEvent extends NetworkEvent {

	public ProjectileComponent projectile;
	public int                 entityID;
	public Vector3f            position;

	public ProjectileHitEvent() {
	}

	public ProjectileHitEvent(ProjectileComponent projectile, int entityID, Vector3f position) {
		this.projectile = projectile;
		this.entityID = entityID;
		this.position = position;
	}

	@Override
	public int getChannel() {
		return StrategyPacketInfo.EVENT_PACKET_CHANNEL;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		entityID = in.readInt();
		position = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
		projectile = new ProjectileComponent();
		projectile.fromStream(in);
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(entityID);
		out.writeFloat(position.getX());
		out.writeFloat(position.getY());
		out.writeFloat(position.getZ());
		projectile.toStream(out);
	}
}
