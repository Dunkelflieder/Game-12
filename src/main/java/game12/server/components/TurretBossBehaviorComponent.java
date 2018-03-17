package game12.server.components;

import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.core.EventTimer;
import game12.core.map.Component;

@ComponentInfo(name = "turretBossBehavior", side = ComponentSide.SERVER)
public class TurretBossBehaviorComponent extends BehaviorComponent {

	public EventTimer moveTimer;
	public EventTimer shootTimer;

	public float moveDirection;
	public float moveState;

	public TurretBossBehaviorComponent() {
		moveTimer = new EventTimer(12, false);
		resetShootTimer();
	}

	public void resetShootTimer() {
		shootTimer = new EventTimer(1, false, 6);
	}

	@Override
	public Component clone() {
		return new TurretBossBehaviorComponent();
	}

}
