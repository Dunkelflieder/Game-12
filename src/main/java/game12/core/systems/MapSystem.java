package game12.core.systems;

import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;
import game12.core.SynchronizedSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MapSystem extends SynchronizedSystem {

	public static final int DOOR = -1;
	public static final int VOID = 0;

	private int   width;
	private int   height;
	private int[] map;

	public MapSystem(int width, int height) {
		this.width = width;
		this.height = height;
		this.map = new int[width * height];

		// test
		this.width = 20;
		this.height = 20;
		map = new int[] {
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, 0, 5, 5, 5, 5, 5, 5, 0,
				0, 1, 1, 1, 1, 1,-1, 2, 2, 2, 2, 2, 0, 5, 5, 5, 5, 5, 5, 0,
				0, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, 0, 5, 5, 5, 5, 5, 5, 0,
				0, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2,-1, 5, 5, 5, 5, 5, 5, 0,
				0, 0, 0,-1, 0, 0, 0, 2, 2, 2, 2, 2, 0, 5, 5, 5, 5, 5, 5, 0,
				0, 3, 3, 3, 3, 3, 0, 2, 2, 2, 2, 2, 0, 5, 5, 5, 5, 5, 5, 0,
				0, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 5, 5, 0,
				0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 4, 4, 4, 4, 4, 4, 0, 5, 5, 0,
				0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 4, 4, 4, 4, 4, 4, 0, 5, 5, 0,
				0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 4, 4, 4, 4, 4, 4, 0, 5, 5, 0,
				0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 4, 4, 4, 4, 4, 4, 0, 5, 5, 0,
				0, 0, 0, 0, 0, 0,-1, 0, 0, 0, 4, 4, 4, 4, 4, 4, 0, 5, 5, 0,
				0, 7, 7, 7, 7, 7, 7, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0,
				0, 7, 7, 7, 7, 7, 7, 0, 8, 8, 8, 0, 6, 6, 6, 6, 6, 6, 6, 0,
				0, 7, 7, 7, 7, 7, 7, 0, 8, 8, 8, 0, 6, 6, 6, 6, 6, 6, 6, 0,
				0, 7, 7, 7, 7, 7, 7, 0, 8, 8, 8,-1, 6, 6, 6, 6, 6, 6, 6, 0,
				0, 7, 7, 7, 7, 7, 7,-1, 8, 8, 8, 0, 6, 6, 6, 6, 6, 6, 6, 0,
				0, 7, 7, 7, 7, 7, 7, 0, 8, 8, 8, 0, 6, 6, 6, 6, 6, 6, 6, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		};
	}

	public int getWidth()  { return width; }

	public int getHeight() { return height; }

	public int get(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= width) return VOID;

		return map[y * width + x];
	}

	@Override
	public void sendNetworkInit(DataOutputStream out) throws IOException {
		NDSDataOutputStream stream = new NDSDataOutputStream(out);
		stream.writeInt(width);
		stream.writeInt(height);
		stream.writeIntArray(map);
	}

	@Override
	public void networkInit(DataInputStream in) throws IOException {
		NDSDataInputStream stream = new NDSDataInputStream(in);
		width = stream.readInt();
		height = stream.readInt();
		map = stream.readIntArray();
	}
}
