package game12.server.components;

import game12.core.EventTimer;
import game12.core.map.Component;
import game12.core.map.Entity;

public class TurretBehaviorComponent extends Component {

	public EventTimer shootTimer;

	public Entity projectile;

	public static final float MAX_SHOOT_DELAY = 5f;
	public float shootDelay;

	public TurretBehaviorComponent() {
		this.shootTimer = new EventTimer(8.0f, true);
	}

	@Override
	protected void cleanup() {
		if (projectile != null) getEntity().getMap().getEntityList().remove(projectile.getID());
	}

	@Override
	public Component clone() {
		return new TurretBehaviorComponent();
	}

}
