package game12.client.systems;

import de.nerogar.noise.render.*;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderable;
import game12.core.LogicSystem;
import game12.core.systems.MapSystem;

public class MapRenderSystem extends LogicSystem {

	@Override
	public void init() {
		RenderSystem renderSystem = getContainer().getSystem(RenderSystem.class);

		MapSystem mapSystem = getContainer().getSystem(MapSystem.class);

		Mesh mesh = createMesh();
		Texture2D white = Texture2DLoader.loadTexture("<white.png>");
		Texture2D normal = Texture2DLoader.loadTexture("<normal.png>");
		Texture2D red = Texture2DLoader.loadTexture("<red.png>");

		for (int x = 0; x < mapSystem.getWidth(); x++) {
			for (int y = 0; y < mapSystem.getHeight(); y++) {
				int tile = mapSystem.get(x, y);

				Texture2D color = tile == 0 ? white : red;

				DeferredContainer container = new DeferredContainer(
						mesh,
						null,
						color,
						normal,
						red
				);
				RenderProperties3f renderProperties = new RenderProperties3f(0, 0, 0, x, 0, y);
				DeferredRenderable renderable = new DeferredRenderable(
						container,
						renderProperties
				);

				renderSystem.getRenderer().addObject(renderable);
			}
		}

	}

	private Mesh createMesh() {
		VertexList vertexList = new VertexList();
		int index1 = vertexList.addVertex(0, 0, 0, 0, 0, 0, 0, 0);
		int index2 = vertexList.addVertex(1, 0, 0, 1, 0, 0, 0, 0);
		int index3 = vertexList.addVertex(1, 0, 1, 1, 1, 0, 0, 0);
		int index4 = vertexList.addVertex(0, 0, 1, 0, 1, 0, 0, 0);
		vertexList.addIndex(index3, index2, index1);
		vertexList.addIndex(index4, index3, index1);
		return new Mesh(
				vertexList.getIndexCount(),
				vertexList.getVertexCount(),
				vertexList.getIndexArray(),
				vertexList.getPositionArray(),
				vertexList.getUVArray()
		);
	}

}
