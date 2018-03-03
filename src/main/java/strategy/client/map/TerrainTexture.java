package strategy.client.map;

import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.render.Texture2DLoader;

public class TerrainTexture {

	public static Texture2D colorTexture;
	public static Texture2D normalTexture;
	public static Texture2D lightTexture;

	public static void loadTextures() {
		colorTexture = Texture2DLoader.loadTexture("res/blocks/textures/colorTest.png", Texture2D.InterpolationType.NEAREST_MIPMAP);
		normalTexture = Texture2DLoader.loadTexture("res/blocks/textures/normalTest.png", Texture2D.InterpolationType.LINEAR_MIPMAP);
		lightTexture = Texture2DLoader.loadTexture("res/blocks/textures/lightTest.png", Texture2D.InterpolationType.LINEAR_MIPMAP);
	}

}
