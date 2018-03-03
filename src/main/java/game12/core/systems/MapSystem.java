package game12.core.systems;

import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;
import game12.core.SynchronizedSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MapSystem extends SynchronizedSystem {

	private int   width;
	private int   height;
	private int[] map;

	public MapSystem(int width, int height) {
		this.width = 4;//width;
		this.height = 4;//height;
		this.map = new int[width * height];
		map = new int[] {
				0, 0, 0, 1,
				0, 0, 0, 1,
				0, 0, 1, 1,
				1, 1, 1, 1
		};
	}

	public int getWidth()  { return width; }

	public int getHeight() { return height; }

	public int get(int x, int y) {
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
