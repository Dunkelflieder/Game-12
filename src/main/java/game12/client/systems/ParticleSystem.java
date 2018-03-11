package game12.client.systems;

import de.nerogar.noise.util.Vector3f;
import game12.client.components.ParticlePhysicsComponent;
import game12.client.map.ClientMap;
import game12.core.EntityFactorySystem;
import game12.core.LogicSystem;
import game12.core.components.LifetimeComponent;
import game12.core.event.ProjectileHitEvent;
import game12.core.map.Entity;
import game12.core.systems.GameObjectsSystem;
import game12.core.utils.VectorUtils;
import game12.server.event.HealthChangedEvent;

import java.util.Random;

public class ParticleSystem extends LogicSystem {

	private static final float RANDOM_LIFETIME_FACTOR = 0.4f;

	private ClientMap map;
	private Random random = new Random();
	private EntityFactorySystem entityFactory;

	public ParticleSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {

		entityFactory = getContainer().getSystem(EntityFactorySystem.class);
		GameObjectsSystem gameObjectsSystem = map.getGameSystem(GameObjectsSystem.class);
		short blood = gameObjectsSystem.getID("blood-particle");

		getEventManager().register(ProjectileHitEvent.class, event -> spawnParticles(blood, 10, event.position, event.projectile.direction));
		getEventManager().register(HealthChangedEvent.class, event -> {
			System.out.println("Damage taken: " + event.entityID + " from " + event.oldHealth + " to " + event.newHealth);
		});
	}

	private void spawnParticles(short id, int num, Vector3f pos, Vector3f dir) {
		for (int i = 0; i < num; i++) {
			Entity entity = entityFactory.createEntity(id, pos.getX(), pos.getY(), pos.getZ());
			LifetimeComponent lifetimeComponent = entity.getComponent(LifetimeComponent.class);
			lifetimeComponent.lifetime *= RANDOM_LIFETIME_FACTOR * (random.nextFloat() - 0.5f) + 1;
			ParticlePhysicsComponent particlePhysicsComponent = entity.getComponent(ParticlePhysicsComponent.class);
			particlePhysicsComponent.velocity = VectorUtils.mutateVector(dir, 5f);
			if (i % 2 == 0) {
				particlePhysicsComponent.velocity.multiply(-1);
			}
		}
	}
}
