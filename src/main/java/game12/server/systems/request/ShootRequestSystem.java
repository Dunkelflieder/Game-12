package game12.server.systems.request;

import game12.core.EntityFactorySystem;
import game12.core.components.ProjectileComponent;
import game12.core.map.Entity;
import game12.core.request.ShootRequestPacket;
import game12.core.systems.GameObjectsSystem;
import game12.server.map.ServerMap;
import game12.server.systems.RequestSystem;

public class ShootRequestSystem extends RequestSystem<ShootRequestPacket> {

	private final ServerMap           map;
	private       EntityFactorySystem entityFactory;
	private       short               projectileBlueprintID;

	public ShootRequestSystem(ServerMap map) {
		super(ShootRequestPacket.class);
		this.map = map;
	}

	@Override
	public void init() {
		super.init();
		entityFactory = getContainer().getSystem(EntityFactorySystem.class);
		projectileBlueprintID = map.getGameSystem(GameObjectsSystem.class).getID("Projectile");
	}

	@Override
	protected void requestFunction(ShootRequestPacket request) {
		Entity entity = entityFactory.createEntity(projectileBlueprintID, request.start.getX(), request.start.getY(), request.start.getZ());
		ProjectileComponent projectile = entity.getComponent(ProjectileComponent.class);
		projectile.fromPlayer = true;
		projectile.direction = request.direction;
	}
}
