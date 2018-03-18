package game12.server.components;

import de.nerogar.noise.util.Vector3f;
import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.Component;

@ComponentInfo(name = "spikeTrapBehavior", side = ComponentSide.SERVER)
public class SpikeTrapBehaviorComponent extends BehaviorComponent {

	enum State {
		INITIAL,
		ENGAGED,
		RESETTING,
	}

	private static final float ENGAGE_SPEED = 10;
	private static final float RESET_SPEED  = 2;

	private State state = State.INITIAL;
	private Vector3f home;
	private Vector3f target;

	public boolean isReady() {
		return state == State.INITIAL;
	}

	public void engageTarget(Vector3f target) {
		this.target = target;
		this.state = State.ENGAGED;
	}

	@Override
	protected void init() {
		super.init();
		PositionComponent positionComponent = getEntity().getComponent(PositionComponent.class);
		home = new Vector3f(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
	}

	@Override
	public Component clone() {
		return new SpikeTrapBehaviorComponent();
	}

	private boolean approachPosition(Vector3f pos, float delta) {
		PositionComponent positionComponent = getEntity().getComponent(PositionComponent.class);
		Vector3f deltaVector = new Vector3f(
				pos.getX() - positionComponent.getX(),
				pos.getY() - positionComponent.getY(),
				pos.getZ() - positionComponent.getZ()
		);
		if (deltaVector.getSquaredValue() < delta * delta) {
			// reached position
			positionComponent.setPosition(pos.getX(), pos.getY(), pos.getZ());
			return true;
		} else {
			deltaVector.setValue(delta);
			positionComponent.setPosition(
					positionComponent.getX() + deltaVector.getX(),
					positionComponent.getY() + deltaVector.getY(),
					positionComponent.getZ() + deltaVector.getZ()
			                             );
		}
		return false;
	}

	public void update(UpdateEvent event) {
		if (state == State.INITIAL) {
			// nothing to do
			return;
		} else if (state == State.ENGAGED) {
			// ATTACK!!!
			boolean finished = approachPosition(target, ENGAGE_SPEED * event.getDelta());
			if (finished) state = State.RESETTING;
		} else if (state == State.RESETTING) {
			// go back to home-point
			boolean finished = approachPosition(home, RESET_SPEED * event.getDelta());
			if (finished) state = State.INITIAL;
		} else {
			throw new IllegalStateException("unknown state: " + state);
		}
		System.out.println("\rState: " + state + ", target: " + target + ", delta: " + event.getDelta());
	}
}
