package game12.core.components;

import de.nerogar.noise.network.Streamable;
import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.util.Vector3f;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProjectileComponent extends Component implements Streamable {

	public float   speed;
	public int     damage;
	public boolean fromPlayer;

	public Vector3f direction;

	public ProjectileComponent() {
		direction = new Vector3f();
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		speed = data.getFloat("speed");
		damage = data.getInt("damage");
	}

	public ProjectileComponent(float speed, int damage, boolean fromPlayer, Vector3f direction) {
		this.speed = speed;
		this.damage = damage;
		this.fromPlayer = fromPlayer;
		this.direction = direction;
	}

	@Override
	public ProjectileComponent clone() {
		return new ProjectileComponent(speed, damage, fromPlayer, direction.clone());
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		speed = in.readFloat();
		damage = in.readInt();
		fromPlayer = in.readBoolean();
		direction = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeFloat(speed);
		out.writeInt(damage);
		out.writeBoolean(fromPlayer);
		out.writeFloat(direction.getX());
		out.writeFloat(direction.getY());
		out.writeFloat(direction.getZ());
	}
}
