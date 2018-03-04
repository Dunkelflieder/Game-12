package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FollowPathComponent extends SynchronizedComponent {

	private int        progressNode;
	private float      progress;
	private Vector3f[] path;
	private float[]    distances;

	private float speed;

	public FollowPathComponent() {
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
