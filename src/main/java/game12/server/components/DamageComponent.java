package game12.server.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import game12.core.map.Component;
import game12.core.misc.DamageType;
import game12.core.systems.GameObjectsSystem;

public class DamageComponent extends Component {

	public int        damage;
	public DamageType damageType;
	public boolean    selfDestruct;
	public boolean    fromPlayer;

	public DamageComponent() {
	}

	public DamageComponent(int damage, DamageType damageType, boolean selfDestruct, boolean fromPlayer) {
		this.damage = damage;
		this.damageType = damageType;
		this.selfDestruct = selfDestruct;
		this.fromPlayer = fromPlayer;
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		damage = data.getInt("damage");
		damageType = DamageType.valueOf(data.getStringUTF8("damageType").toUpperCase());
		selfDestruct = !data.contains("selfDestruct") || data.getBoolean("selfDestruct");
	}

	@Override
	public Component clone() {
		return new DamageComponent(damage, damageType, selfDestruct, fromPlayer);
	}
}
