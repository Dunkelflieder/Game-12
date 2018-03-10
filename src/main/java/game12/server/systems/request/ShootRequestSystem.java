package game12.server.systems.request;

import game12.core.EntityFactorySystem;
import game12.core.components.ProjectileComponent;
import game12.core.map.Entity;
import game12.core.request.ShootRequestPacket;
import game12.core.systems.GameObjectsSystem;
import game12.core.utils.VectorUtils;
import game12.server.map.ServerMap;
import game12.server.systems.RequestSystem;

public class ShootRequestSystem extends RequestSystem<ShootRequestPacket> {

	private final ServerMap           map;
	private       EntityFactorySystem entityFactory;
	private       short               shotgunProjectileBlueprintID;

	public ShootRequestSystem(ServerMap map) {
		super(ShootRequestPacket.class);
		this.map = map;
	}

	@Override
	public void init() {
		super.init();
		entityFactory = getContainer().getSystem(EntityFactorySystem.class);
		shotgunProjectileBlueprintID = map.getGameSystem(GameObjectsSystem.class).getID("pellet-projectile");
	}

	@Override
	protected void requestFunction(ShootRequestPacket request) {
		if (request.shotType == ShootRequestPacket.TYPE_SHOTGUN) {
			final int numPellets = 10;
			for (int i = 0; i < numPellets; i++) {
				Entity entity = entityFactory.createEntity(
						shotgunProjectileBlueprintID,
						request.start.getX() + request.direction.getX() * 0.1f,
						request.start.getY() - 0.2f,
						request.start.getZ() + request.direction.getZ() * 0.1f
				                                          );
				ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
				projectile.fromPlayer = true;
				projectile.direction = VectorUtils.mutateVector(request.direction, 0.4f);
			}
		} else {
			throw new UnsupportedOperationException("Not implemented");
		}
	}
}
