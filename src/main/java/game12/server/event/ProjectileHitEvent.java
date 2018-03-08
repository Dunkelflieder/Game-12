package game12.server.event;

import game12.core.components.ProjectileComponent;
import game12.core.network.NetworkEvent;
import game12.core.network.StrategyPacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProjectileHitEvent extends NetworkEvent {

	public ProjectileComponent projectile;
	public int                 entityID;

	public ProjectileHitEvent() {
	}

	public ProjectileHitEvent(ProjectileComponent projectile, int entityID) {
		this.projectile = projectile;
		this.entityID = entityID;
	}

	@Override
	public int getChannel() {
		return StrategyPacketInfo.EVENT_PACKET_CHANNEL;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		projectile = new ProjectileComponent();
		projectile.fromStream(in);
		entityID = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		projectile.toStream(out);
		out.writeInt(entityID);
	}
}
