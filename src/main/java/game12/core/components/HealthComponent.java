package game12.core.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HealthComponent extends SynchronizedComponent {

	public float health;
	public float maxHealth;

	public HealthComponent() {
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		maxHealth = data.getFloat("maxHealth");
		if (data.contains("health")) {
			health = data.getFloat("health");
		} else {
			health = maxHealth;
		}
	}

	public HealthComponent(float maxHealth, float health) {
		this.maxHealth = maxHealth;
		this.health = health;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		maxHealth = in.readFloat();
		health = in.readFloat();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeFloat(maxHealth);
		out.writeFloat(health);
	}

	@Override
	public Component clone() {
		return new HealthComponent(maxHealth, health);
	}
}
