package game12.client.systems;

import game12.client.components.ParticlePhysicsComponent;
import game12.client.map.ClientMap;
import game12.core.LogicSystem;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.systems.MapSystem;

public class ParticlePhysicsSystem extends LogicSystem {

	private static final float RANDOM_LIFETIME_FACTOR = 0.4f;

	private ClientMap map;
	private MapSystem mapSystem;

	public ParticlePhysicsSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		mapSystem = getContainer().getSystem(MapSystem.class);
		getEventManager().register(UpdateEvent.class, this::update);

	}

	private void update(UpdateEvent event) {
		for (ParticlePhysicsComponent physicsComponent : map.getEntityList().getComponents(ParticlePhysicsComponent.class)) {
			PositionComponent position = physicsComponent.getEntity().getComponent(PositionComponent.class);
			physicsComponent.update(event.getDelta());
			float newX = position.getX() + physicsComponent.velocity.getX() * event.getDelta();
			float newY = position.getY() + physicsComponent.velocity.getY() * event.getDelta();
			float newZ = position.getZ() + physicsComponent.velocity.getZ() * event.getDelta();
			if (newY > 0 && mapSystem.isWalkable((int) newX, (int) newZ, true)) {
				position.setPosition(newX, newY, newZ);
			}
		}
	}

}
