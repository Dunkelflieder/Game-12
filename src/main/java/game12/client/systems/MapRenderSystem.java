package game12.client.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderable;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import game12.client.event.BeforeRenderEvent;
import game12.core.LogicSystem;
import game12.core.event.MapChangeEvent;
import game12.core.systems.MapSystem;

public class MapRenderSystem extends LogicSystem {

	private static final float WALL_OFFSET = 0.3f;

	private EventListener<MapChangeEvent>    mapChangeEventListener;
	private EventListener<BeforeRenderEvent> beforeRenderEventListener;

	private boolean shouldRecreate = true;

	//private Texture2D white  = Texture2DLoader.loadTexture("<white.png>");
	//private Texture2D normal = Texture2DLoader.loadTexture("<normal.png>");
	private Texture2D color  = Texture2DLoader.loadTexture("res/wall/color.png", Texture2D.InterpolationType.NEAREST_MIPMAP);
	private Texture2D normal = Texture2DLoader.loadTexture("res/wall/normal.png", Texture2D.InterpolationType.NEAREST_MIPMAP);
	private Texture2D red    = Texture2DLoader.loadTexture("<red.png>");

	private DeferredRenderer   renderer;
	private DeferredRenderable mapRenderable;

	private MapSystem mapSystem;

	@Override
	public void init() {
		this.mapChangeEventListener = this::onMapChange;
		getEventManager().registerImmediate(MapChangeEvent.class, mapChangeEventListener);

		this.beforeRenderEventListener = this::onBeforeRender;
		getEventManager().register(BeforeRenderEvent.class, beforeRenderEventListener);

		RenderSystem renderSystem = getContainer().getSystem(RenderSystem.class);
		this.renderer = renderSystem.getRenderer();

		mapSystem = getContainer().getSystem(MapSystem.class);
	}

	private void onMapChange(MapChangeEvent event) {
		shouldRecreate = true;
	}

	private void onBeforeRender(BeforeRenderEvent event) {

		// TODO remove this (debug)
		shouldRecreate = true;

		if (shouldRecreate) {
			if (mapRenderable != null) {
				renderer.removeObject(mapRenderable);
			}

			Mesh mesh = createMesh();
			DeferredContainer container = new DeferredContainer(
					mesh,
					null,
					color,
					normal,
					red
			);
			RenderProperties3f renderProperties = new RenderProperties3f(0, 0, 0, 0, 0, 0);
			mapRenderable = new DeferredRenderable(
					container,
					renderProperties
			);

			renderer.addObject(mapRenderable);

			shouldRecreate = false;
		}
	}

	private Mesh createMesh() {
		VertexList vertexList = new VertexList();

		for (int x = 0; x < mapSystem.getWidth(); x++) {
			for (int y = 0; y < mapSystem.getHeight(); y++) {
				int tile = mapSystem.get(x, y);

				if (tile > MapSystem.VOID) {
					int indexNN = vertexList.addVertex(x + 0, 0, y + 0, 0, 0, 0, 0, 0);
					int indexPN = vertexList.addVertex(x + 1, 0, y + 0, 1, 0, 0, 0, 0);
					int indexPP = vertexList.addVertex(x + 1, 0, y + 1, 1, 1, 0, 0, 0);
					int indexNP = vertexList.addVertex(x + 0, 0, y + 1, 0, 1, 0, 0, 0);

					vertexList.addIndex(indexNN, indexNP, indexPP);
					vertexList.addIndex(indexNN, indexPP, indexPN);
				} else {
					createWall(vertexList, x + 0, y + 0, x + 0, y + 1, 1, 0, mapSystem.get(x - 1, y) <= 0);
					createWall(vertexList, x + 0, y + 1, x + 1, y + 1, 0, -1, mapSystem.get(x, y + 1) <= 0);
					createWall(vertexList, x + 1, y + 1, x + 1, y + 0, -1, 0, mapSystem.get(x + 1, y) <= 0);
					createWall(vertexList, x + 1, y + 0, x + 0, y + 0, 0, 1, mapSystem.get(x, y - 1) <= 0);
				}
			}
		}

		return new Mesh(
				vertexList.getIndexCount(),
				vertexList.getVertexCount(),
				vertexList.getIndexArray(),
				vertexList.getPositionArray(),
				vertexList.getUVArray()
		);
	}

	private void createWall(VertexList vertexList, float x0, float y0, float x1, float y1, float dx, float dy, boolean connect) {
		// wall
		if (!connect) {
			int index1 = vertexList.addVertex(x0, 0, y0, 0, 0, 0, 0, 0);
			int index2 = vertexList.addVertex(x1, 0, y1, 1, 0, 0, 0, 0);
			int index3 = vertexList.addVertex(x1 + (dx + x0 - x1) * WALL_OFFSET, 1, y1 + (dy + y0 - y1) * WALL_OFFSET, 1 - WALL_OFFSET, 1, 0, 0, 0);
			int index4 = vertexList.addVertex(x0 + (dx + x1 - x0) * WALL_OFFSET, 1, y0 + (dy + y1 - y0) * WALL_OFFSET, WALL_OFFSET, 1, 0, 0, 0);

			vertexList.addIndex(index1, index2, index3);
			vertexList.addIndex(index1, index3, index4);
		} else {

			// wall connection
			{
				int index1 = vertexList.addVertex(x0, 0, y0, 1, 0, 0, 0, 0);
				int index2 = vertexList.addVertex(x0 + (x1 - x0) * WALL_OFFSET, 1, y0 + (y1 - y0) * WALL_OFFSET, 1, 1, 0, 0, 0);
				int index3 = vertexList.addVertex(x0 + (dx + x1 - x0) * WALL_OFFSET, 1, y0 + (dy + y1 - y0) * WALL_OFFSET, 1 - WALL_OFFSET, 1, 0, 0, 0);

				vertexList.addIndex(index1, index2, index3);

				int index4 = vertexList.addVertex(x1, 0, y1, 0, 0, 0, 0, 0);
				int index5 = vertexList.addVertex(x1 + (dx + x0 - x1) * WALL_OFFSET, 1, y1 + (dy + y0 - y1) * WALL_OFFSET, WALL_OFFSET, 1, 0, 0, 0);
				int index6 = vertexList.addVertex(x1 + (x0 - x1) * WALL_OFFSET, 1, y1 + (y0 - y1) * WALL_OFFSET, 0, 1, 0, 0, 0);

				vertexList.addIndex(index4, index5, index6);
			}

			// ceiling (
			/*{
				int index1 = vertexList.addVertex(x0 + (x1 - x0) * WALL_OFFSET, 1, y0 + (y1 - y0) * WALL_OFFSET, 0, 0, 0, 0, 0);
				int index2 = vertexList.addVertex(x1 + (x0 - x1) * WALL_OFFSET, 1, y1 + (y0 - y1) * WALL_OFFSET, 1, 0, 0, 0, 0);
				int index3 = vertexList.addVertex(x1 + (dx + x0 - x1) * WALL_OFFSET, 1, y1 + (dy + y0 - y1) * WALL_OFFSET, 1, 1, 0, 0, 0);
				int index4 = vertexList.addVertex(x0 + (dx + x1 - x0) * WALL_OFFSET, 1, y0 + (dy + y1 - y0) * WALL_OFFSET, 0, 1, 0, 0, 0);

				vertexList.addIndex(index1, index2, index3);
				vertexList.addIndex(index1, index3, index4);
			}*/
		}
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(MapChangeEvent.class, mapChangeEventListener);
		getEventManager().unregister(BeforeRenderEvent.class, beforeRenderEventListener);
	}
}
