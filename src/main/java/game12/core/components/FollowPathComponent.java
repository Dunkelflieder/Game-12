package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.annotations.ComponentInfo;
import game12.annotations.ComponentSide;
import game12.core.EventTimer;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;
import game12.core.utils.Vector2i;
import game12.server.ai.Pathfinder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

@ComponentInfo(name = "followPath", side = ComponentSide.CORE)
public class FollowPathComponent extends SynchronizedComponent {

	private int        progressNode;
	private float      progress;
	private Vector3f[] path;
	private float[]    distances;

	private float speed;

	EventTimer timer = new EventTimer(0.5f, true, -1);
	;

	public FollowPathComponent() {
	}

	public void updatePath(Vector3f targetPosition, Pathfinder pathfinder) {
		PositionComponent positionComponent = getEntity().getComponent(PositionComponent.class);
		float height = positionComponent.getY();
		while (timer.trigger()) {
			Vector2i intPos = Vector2i.of((int) positionComponent.getX(), (int) positionComponent.getZ());
			List<Vector2i> path = pathfinder.getPath(intPos, Vector2i.of((int) targetPosition.getX(), (int) targetPosition.getZ()));
			if (path == null || path.isEmpty()) {
				stop();
			} else {
				Vector3f[] path3fArray = new Vector3f[path.size() + 1];
				for (int i = 0; i < path.size(); i++) {
					Vector2i vec = path.get(i);
					path3fArray[i + 1] = new Vector3f(vec.x + 0.5f, height, vec.y + 0.5f);
				}
				path3fArray[0] = new Vector3f(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
				path3fArray[path3fArray.length - 1] = new Vector3f(targetPosition.getX(), height, targetPosition.getZ());
				newPath(path3fArray);
			}
		}
	}

	public FollowPathComponent(float speed) {
		this.speed = speed;
	}

	@Override
	protected void initSystems() {
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		speed = data.getFloat("speed");
	}

	public void newPath(Vector3f[] path) {
		this.path = path;
		this.distances = new float[path.length - 1];

		for (int i = 0; i < path.length - 1; i++) {
			float x = path[i].getX() - path[i + 1].getX();
			float y = path[i].getY() - path[i + 1].getY();
			float z = path[i].getZ() - path[i + 1].getZ();

			distances[i] = (float) Math.sqrt(x * x + y * y + z * z);
		}

		progress = 0;
		progressNode = 0;
	}

	public void stop() {
		path = null;
	}

	public void update(float timeDelta) {
		timer.update(timeDelta);
		if (path == null) return;

		float deltaProgress = timeDelta * speed;
		progress += deltaProgress;

		while (progress >= distances[progressNode]) {
			progress -= distances[progressNode];
			progressNode++;

			if (progressNode >= path.length - 1) {
				progressNode = path.length - 1;
				progress = 0;
				break;
			}

		}

		float x, y, z;
		if (progressNode < path.length - 1) {
			x = path[progressNode].getX() + (progress / distances[progressNode]) * (path[progressNode + 1].getX() - path[progressNode].getX());
			y = path[progressNode].getY() + (progress / distances[progressNode]) * (path[progressNode + 1].getY() - path[progressNode].getY());
			z = path[progressNode].getZ() + (progress / distances[progressNode]) * (path[progressNode + 1].getZ() - path[progressNode].getZ());
		} else {
			x = path[path.length - 1].getX();
			y = path[path.length - 1].getY();
			z = path[path.length - 1].getZ();
			stop();
		}

		getEntity().getComponent(PositionComponent.class).setPosition(x, y, z);

	}

	@Override
	public Component clone() {
		return new FollowPathComponent(speed);
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
	}
}
