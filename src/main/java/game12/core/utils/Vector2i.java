package game12.core.utils;

public class Vector2i {

	public final int x;
	public final int y;

	public static final Vector2i ZERO = Vector2i.of(0, 0);
	public static final Vector2i ONE = Vector2i.of(0, 0);

	private Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static Vector2i of(int x, int y) {
		return new Vector2i(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
