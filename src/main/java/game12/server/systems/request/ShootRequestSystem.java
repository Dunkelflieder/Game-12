package game12.server.systems.request;

import de.nerogar.noise.util.Vector3f;
import game12.core.EntityFactorySystem;
import game12.core.components.PositionComponent;
import game12.core.components.ProjectileComponent;
import game12.core.map.Entity;
import game12.core.request.ShootRequestPacket;
import game12.core.systems.GameObjectsSystem;
import game12.server.map.ServerMap;
import game12.server.systems.RequestSystem;

import java.util.Random;

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
		shotgunProjectileBlueprintID = map.getGameSystem(GameObjectsSystem.class).getID("shotgun-projectile");
	}

	private static final Random RANDOM = new Random();

	private Vector3f mutateVector(Vector3f vector, float strength) {
		Vector3f newVector = new Vector3f(
				vector.getX() + (RANDOM.nextFloat() - 0.5f) * strength,
				vector.getY() + (RANDOM.nextFloat() - 0.5f) * strength,
				vector.getZ() + (RANDOM.nextFloat() - 0.5f) * strength
		);
		newVector.normalize();
		return newVector;
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
				PositionComponent position = entity.getComponent(PositionComponent.class);
				position.setScale(0.05f);
				ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
				projectile.fromPlayer = true;
				projectile.direction = mutateVector(request.direction, 0.4f);
			}
		} else {
			throw new UnsupportedOperationException("Not implemented");
		}
	}
}
