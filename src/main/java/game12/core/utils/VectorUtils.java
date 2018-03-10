package game12.core.utils;

import de.nerogar.noise.util.Vector3f;

import java.util.Random;

public class VectorUtils {

	private static final Random RANDOM = new Random();

	public static Vector3f mutateVector(Vector3f vector, float strength) {
		Vector3f newVector = new Vector3f(
				vector.getX() + (RANDOM.nextFloat() - 0.5f) * strength,
				vector.getY() + (RANDOM.nextFloat() - 0.5f) * strength,
				vector.getZ() + (RANDOM.nextFloat() - 0.5f) * strength
		);
		newVector.normalize();
		return newVector;
	}
}
