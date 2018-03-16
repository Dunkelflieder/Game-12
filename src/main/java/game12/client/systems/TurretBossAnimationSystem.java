package game12.client.systems;

import game12.client.components.TurretBossAnimationComponent;
import game12.client.event.BeforeRenderEvent;
import game12.client.map.ClientMap;
import game12.core.LogicSystem;
import game12.core.components.PositionComponent;
import game12.core.map.Entity;

public class TurretBossAnimationSystem extends LogicSystem {

	private ClientMap map;

	public TurretBossAnimationSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		getEventManager().register(BeforeRenderEvent.class, this::beforeRender);
	}

	private void beforeRender(BeforeRenderEvent event) {
		for (TurretBossAnimationComponent component : map.getEntityList().getComponents(TurretBossAnimationComponent.class)) {

			PositionComponent position = component.getEntity().getComponent(PositionComponent.class);

			setPosition(component.turretBossBase, position, 0.0f, 0.0f, component.scale);
			setPosition(component.turretBossSpikes1, position, 1.0f, 1.0f * event.getDelta(), component.scale);
			setPosition(component.turretBossRing1, position, 1.7f, 0.0f, component.scale);
			setPosition(component.turretBossSpikes2, position, 2.0f, -1.0f * event.getDelta(), component.scale);
			setPosition(component.turretBossRing2, position, 2.7f, 0.0f, component.scale);
			setPosition(component.turretBossTop, position, 3.0f, 1.0f * event.getDelta(), component.scale);
		}
	}

	private void setPosition(Entity entity, PositionComponent position, float yOffset, float rotationDelta, float scale) {
		PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

		positionComponent.setPosition(
				position.getX(), position.getY() + yOffset * scale, position.getZ(),
				positionComponent.getRotation() + rotationDelta,
				scale
		                             );
	}

}
