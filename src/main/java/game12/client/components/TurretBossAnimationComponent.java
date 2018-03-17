package game12.client.components;

import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.core.EntityFactorySystem;
import game12.core.map.Component;
import game12.core.map.Entity;
import game12.core.systems.GameObjectsSystem;

@ComponentInfo(name = "turretBossAnimation", side = ComponentSide.CLIENT)
public class TurretBossAnimationComponent extends Component {

	public Entity turretBossBase;
	public Entity turretBossSpikes1;
	public Entity turretBossRing1;
	public Entity turretBossSpikes2;
	public Entity turretBossRing2;
	public Entity turretBossTop;

	public float scale;

	@Override
	protected void init() {
		EntityFactorySystem entityFactory = getEntity().getMap().getSystem(EntityFactorySystem.class);

		scale = 0.5f;

		short turretBossBaseBlueprintId = getEntity().getMap().getGameSystem(GameObjectsSystem.class).getID("turret-boss-base");
		short turretBossSpikesBlueprintId = getEntity().getMap().getGameSystem(GameObjectsSystem.class).getID("turret-boss-spikes");
		short turretBossRingBlueprintId = getEntity().getMap().getGameSystem(GameObjectsSystem.class).getID("turret-boss-ring");
		short turretBossTopBlueprintId = getEntity().getMap().getGameSystem(GameObjectsSystem.class).getID("turret-boss-top");

		turretBossBase = entityFactory.createEntity(turretBossBaseBlueprintId, 10, 0 * scale, 10);
		turretBossSpikes1 = entityFactory.createEntity(turretBossSpikesBlueprintId, 10, 1 * scale, 10);
		turretBossRing1 = entityFactory.createEntity(turretBossRingBlueprintId, 10, 1.7f * scale, 10);
		turretBossSpikes2 = entityFactory.createEntity(turretBossSpikesBlueprintId, 10, 2 * scale, 10);
		turretBossRing2 = entityFactory.createEntity(turretBossRingBlueprintId, 10, 2.7f * scale, 10);
		turretBossTop = entityFactory.createEntity(turretBossTopBlueprintId, 10, 3 * scale, 10);

	}

	@Override
	protected void cleanup() {
		getEntity().getMap().getEntityList().remove(turretBossBase.getID());
		getEntity().getMap().getEntityList().remove(turretBossSpikes1.getID());
		getEntity().getMap().getEntityList().remove(turretBossRing1.getID());
		getEntity().getMap().getEntityList().remove(turretBossSpikes2.getID());
		getEntity().getMap().getEntityList().remove(turretBossRing2.getID());
		getEntity().getMap().getEntityList().remove(turretBossTop.getID());
	}

	@Override
	public Component clone() {
		return new TurretBossAnimationComponent();
	}

}
