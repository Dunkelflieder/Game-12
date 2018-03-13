package game12.core.misc;

public enum DamageType {
	RANGED(1),
	MELEE(2),;

	public final int id;

	DamageType(int id) {
		this.id = id;
	}

	public static DamageType fromId(int id) {
		for (DamageType damageType : values()) {
			if (damageType.id == id) return damageType;
		}
		throw new IllegalArgumentException("unknown Damage Type ID: " + id);
	}
}
