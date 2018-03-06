package game12.server.systems;

import de.nerogar.noise.util.Vector3f;
import game12.core.LogicSystem;
import game12.core.components.FollowPathComponent;
import game12.core.components.PlayerComponent;
import game12.core.components.PositionComponent;
import game12.core.event.MapChangeEvent;
import game12.core.event.UpdateEvent;
import game12.core.systems.MapSystem;
import game12.core.utils.Vector2i;
import game12.server.ai.Pathfinder;
import game12.server.map.ServerMap;

/**
 * Calculates and sets enemies' paths towards the player once in a while.
 */
public class EnemyPathingSystem extends LogicSystem {

	private final ServerMap  map;
	private       Pathfinder pathfinder;
	private       MapSystem  mapSystem;

	public EnemyPathingSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		mapSystem = getContainer().getSystem(MapSystem.class);
		pathfinder = new Pathfinder(mapSystem.getWidth(), mapSystem.getHeight());

		getEventManager().register(UpdateEvent.class, this::update);
		getEventManager().register(MapChangeEvent.class, event -> updatePathfinder());

		updatePathfinder();
	}

	private void updatePathfinder() {
		int[] costSquare = new int[mapSystem.getWidth() * mapSystem.getHeight()];
		for (int x = 0; x < mapSystem.getWidth(); x++) {
			for (int y = 0; y < mapSystem.getHeight(); y++) {
				costSquare[x + y * mapSystem.getWidth()] = mapSystem.isWalkable(x, y, false) ? 1 : -1;
			}
		}
		Vector2i mapDimensions = Vector2i.of(mapSystem.getWidth(), mapSystem.getHeight());
		pathfinder.update(costSquare, Vector2i.ZERO, mapDimensions);
	}

	public void update(UpdateEvent event) {
		PlayerComponent player = map.getEntityList().getComponents(PlayerComponent.class).iterator().next();
		PositionComponent playerPositionComponent = player.getEntity().getComponent(PositionComponent.class);
		Vector3f playerPosition = new Vector3f(playerPositionComponent.getX(), playerPositionComponent.getY(), playerPositionComponent.getZ());
		for (FollowPathComponent followPathComponent : map.getEntityList().getComponents(FollowPathComponent.class)) {
			followPathComponent.update(event.getDelta());
			followPathComponent.updatePath(playerPosition, pathfinder);
		}
	}
}
