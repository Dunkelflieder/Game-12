package game12.server.systems;

import de.nerogar.noise.util.Vector3f;
import game12.core.EntityFactorySystem;
import game12.core.EventTimer;
import game12.core.LogicSystem;
import game12.core.components.FollowPathComponent;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.systems.GameObjectsSystem;
import game12.core.systems.MapSystem;
import game12.core.utils.Vector2i;
import game12.server.ai.Pathfinder;
import game12.server.map.ServerMap;

import java.util.List;

/**
 * Calculates and sets enemies' paths towards the player once in a while.
 */
public class EnemyPathingSystem extends LogicSystem {

	private final ServerMap  map;
	private       EventTimer timer;
	private       Pathfinder pathfinder;
	private       MapSystem  mapSystem;

	public EnemyPathingSystem(ServerMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		timer = new EventTimer(0.1f, true, -1);

		mapSystem = getContainer().getSystem(MapSystem.class);
		pathfinder = new Pathfinder(mapSystem.getWidth(), mapSystem.getHeight());

		getEventManager().register(UpdateEvent.class, this::update);
	}

	private void updatePathfinder() {
		int[] costSquare = new int[mapSystem.getWidth() * mapSystem.getHeight()];
		for (int x = 0; x < mapSystem.getWidth(); x++) {
			for (int y = 0; y < mapSystem.getHeight(); y++) {
				int tile = mapSystem.get(x, y);
				costSquare[x + y * mapSystem.getWidth()] =
						tile == MapSystem.VOID ? -1 :
								tile == MapSystem.DOOR ? 1 :
										1;
			}
		}
		Vector2i mapDimensions = Vector2i.of(mapSystem.getWidth(), mapSystem.getHeight());
		pathfinder.update(costSquare, Vector2i.ZERO, mapDimensions);
	}

	boolean initFlag = false;

	public void update(UpdateEvent event) {
		if (!initFlag) {
			initFlag = true;
			EntityFactorySystem entityFactorySystem = getContainer().getSystem(EntityFactorySystem.class);
			short testEnemyId = map.getGameSystem(GameObjectsSystem.class).getID("TestEnemy");
			entityFactorySystem.createEntity(testEnemyId, 1, 0, 1);
			updatePaths();
		}
		for (FollowPathComponent followPathComponent : map.getEntityList().getComponents(FollowPathComponent.class)) {
			followPathComponent.update(event.getDelta());
		}
		timer.update(event.getDelta());
		while (timer.trigger()) {
			//			updatePaths();
		}

	}

	public void updatePaths() {
		// TODO update on map change
		updatePathfinder();

		Vector2i goal = Vector2i.of(11, 11); // TODO

		for (FollowPathComponent followPathComponent : map.getEntityList().getComponents(FollowPathComponent.class)) {
			PositionComponent positionComponent = followPathComponent.getEntity().getComponent(PositionComponent.class);
			Vector2i intPos = Vector2i.of((int) positionComponent.getX(), (int) positionComponent.getZ());
			List<Vector2i> path = pathfinder.getPath(intPos, goal);
			if (path == null || path.size() <= 1) {
				followPathComponent.stop();
			} else {
				Vector3f[] path3fArray = new Vector3f[path.size()];
				for (int i = 0; i < path.size(); i++) {
					Vector2i vec = path.get(i);
					path3fArray[i] = new Vector3f(vec.x + 0.5f, 0, vec.y + 0.5f);
				}
				path3fArray[0] = new Vector3f(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
				followPathComponent.newPath(path3fArray);
			}
		}
	}
}
