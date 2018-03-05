package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HealthComponent extends SynchronizedComponent {

	public int health;
	public int maxHealth;

	public HealthComponent() {
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		maxHealth = data.getInt("maxHealth");
		if (data.contains("health")) {
			health = data.getInt("health");
		}
	}

	public HealthComponent(int maxHealth, int health) {
		this.maxHealth = maxHealth;
		this.health = health;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		maxHealth = in.readInt();
		health = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(maxHealth);
		out.writeInt(health);
	}

	@Override
	public Component clone() {
		return new HealthComponent(maxHealth, health);
	}
}
