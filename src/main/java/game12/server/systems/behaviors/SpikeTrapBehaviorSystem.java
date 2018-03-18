package game12.server.systems.behaviors;

import de.nerogar.noise.util.Vector3f;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.systems.MapSystem;
import game12.core.systems.PlayerSystem;
import game12.core.utils.Vector2i;
import game12.server.components.SpikeTrapBehaviorComponent;
import game12.server.map.ServerMap;

public class SpikeTrapBehaviorSystem extends BehaviorSystem<SpikeTrapBehaviorComponent> {

	private PlayerSystem playerSystem;
	private MapSystem    mapSystem;

	public SpikeTrapBehaviorSystem(ServerMap map) {
		super(SpikeTrapBehaviorComponent.class, map);
	}

	@Override
	public void init() {
		super.init();
		playerSystem = getContainer().getSystem(PlayerSystem.class);
		mapSystem = getContainer().getSystem(MapSystem.class);
	}

	@Override
	protected void behaviourFunction(UpdateEvent event, SpikeTrapBehaviorComponent behaviour) {

		behaviour.update(event);

		if (!behaviour.isReady()) {
			// don't search for a target
			return;
		}

		Vector3f playerPosition = playerSystem.getPlayerPosition();
		int playerX = (int) playerPosition.getX();
		int playerY = (int) playerPosition.getZ();

		PositionComponent trapPosition = behaviour.getEntity().getComponent(PositionComponent.class);
		int trapX = (int) trapPosition.getX();
		int trapY = (int) trapPosition.getZ();

		final Vector2i diff = Vector2i.of(playerX - trapX, playerY - trapY);
		final Vector2i diffSig = Vector2i.of((int) Math.signum(diff.x), (int) Math.signum(diff.y));

		if (diff.x != 0 && diff.y != 0) {
			// not straight
			return;
		}

		boolean walkable = true;
		if (diff.x == 0) {
			if (diff.y > 0) {
				for (int offset = 0; offset < diff.y; offset++) {
					walkable &= mapSystem.isWalkable(trapX, trapY + offset, false);
				}
			} else {
				for (int offset = 0; offset > diff.y; offset--) {
					walkable &= mapSystem.isWalkable(trapX, trapY + offset, false);
				}
			}
		} else {
			if (diff.x > 0) {
				for (int offset = 0; offset < diff.x; offset++) {
					walkable &= mapSystem.isWalkable(trapX + offset, trapY, false);
				}
			} else {
				for (int offset = 0; offset > diff.x; offset--) {
					walkable &= mapSystem.isWalkable(trapX + offset, trapY, false);
				}
			}
		}

		if (!walkable) {
			return;
		}

		Vector3f target = new Vector3f(trapPosition.getX(), trapPosition.getY(), trapPosition.getZ());
		target.addX(diff.x);
		target.addZ(diff.y);
		// extend target towards nearest wall
		while (mapSystem.isWalkable((int) target.getX() + diffSig.x, (int) target.getZ() + diffSig.y, false)) {
			target.addX(diffSig.x);
			target.addZ(diffSig.y);
		}

		behaviour.engageTarget(target);

	}
}
