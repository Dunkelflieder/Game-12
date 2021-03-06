package game12.server.components;

import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.core.EventTimer;
import game12.core.map.Component;
import game12.core.map.Entity;

@ComponentInfo(name = "turretBehavior", side = ComponentSide.SERVER)
public class TurretBehaviorComponent extends BehaviorComponent {

	public EventTimer shootTimer;

	public Entity projectile;

	public static final float MAX_SHOOT_DELAY = 5f;
	public float shootDelay;

	public TurretBehaviorComponent() {
		this.shootTimer = new EventTimer(8.0f, true);
	}

	@Override
	protected void cleanup() {
		if (projectile != null && projectile.isValid()) {
			getEntity().getMap().getEntityList().remove(projectile.getID());
		}
	}

	@Override
	public Component clone() {
		return new TurretBehaviorComponent();
	}

}
