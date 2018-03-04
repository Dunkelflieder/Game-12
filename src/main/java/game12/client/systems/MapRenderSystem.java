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
	private static final int   TILE_COUNT  = 8;
	private static final float TILE_SIZE   = 1f / TILE_COUNT;

	private EventListener<MapChangeEvent>    mapChangeEventListener;
	private EventListener<BeforeRenderEvent> beforeRenderEventListener;

	private boolean shouldRecreate = true;

	private Texture2D color  = Texture2DLoader.loadTexture("res/map/color.png", Texture2D.InterpolationType.NEAREST_MIPMAP);
	private Texture2D normal = Texture2DLoader.loadTexture("res/map/normal.png", Texture2D.InterpolationType.NEAREST_MIPMAP);
	private Texture2D red    = Texture2DLoader.loadTexture("<red.png>");

	private Shader mapShader = DeferredContainer.createSurfaceShader("shaders/map/map.vert", "shaders/map/map.frag", true);

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

		mapShader.activate();
		mapShader.setUniform1f("u_tileSize", TILE_SIZE);
		mapShader.deactivate();

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
					mapShader,
					color,
					normal,
					red,
					true,
					DeferredContainer.OptimizationStrategy.OPTIMIZATION_ONE
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

				if (tile == MapSystem.VOID) {
					createWall(vertexList, x + 0, y + 0, x + 0, y + 1, 1, 0,
					           mapSystem.get(x - 1, y - 1) > MapSystem.VOID || mapSystem.get(x, y - 1) > MapSystem.VOID,
					           mapSystem.get(x - 1, y + 1) > MapSystem.VOID || mapSystem.get(x, y + 1) > MapSystem.VOID,
					           mapSystem.get(x - 1, y) <= MapSystem.VOID

					          );
					createWall(vertexList, x + 0, y + 1, x + 1, y + 1, 0, -1,
					           mapSystem.get(x - 1, y + 1) > MapSystem.VOID || mapSystem.get(x - 1, y) > MapSystem.VOID,
					           mapSystem.get(x + 1, y + 1) > MapSystem.VOID || mapSystem.get(x + 1, y) > MapSystem.VOID,
					           mapSystem.get(x, y + 1) <= 0
					          );
					createWall(vertexList, x + 1, y + 1, x + 1, y + 0, -1, 0,
					           mapSystem.get(x + 1, y + 1) > MapSystem.VOID || mapSystem.get(x, y + 1) > MapSystem.VOID,
					           mapSystem.get(x + 1, y - 1) > MapSystem.VOID || mapSystem.get(x, y - 1) > MapSystem.VOID,
					           mapSystem.get(x + 1, y) <= 0
					          );
					createWall(vertexList, x + 1, y + 0, x + 0, y + 0, 0, 1,
					           mapSystem.get(x + 1, y - 1) > MapSystem.VOID || mapSystem.get(x + 1, y) > MapSystem.VOID,
					           mapSystem.get(x - 1, y - 1) > MapSystem.VOID || mapSystem.get(x - 1, y) > MapSystem.VOID,
					           mapSystem.get(x, y - 1) <= 0
					          );
				} else {
					float u = 1;
					float v = 7;

					int indexNN = vertexList.addVertex(x + 0, 0, y + 0, u, v + 1, 1, 1, 1);
					int indexPN = vertexList.addVertex(x + 1, 0, y + 0, u + 1, v + 1, 1, 1, 1);
					int indexPP = vertexList.addVertex(x + 1, 0, y + 1, u + 1, v, 1, 1, 1);
					int indexNP = vertexList.addVertex(x + 0, 0, y + 1, u, v, 1, 1, 1);

					vertexList.addIndex(indexNN, indexNP, indexPP);
					vertexList.addIndex(indexNN, indexPP, indexPN);
				}
			}
		}

		Mesh mesh = new Mesh(
				vertexList.getIndexCount(),
				vertexList.getVertexCount(),
				vertexList.getIndexArray(),
				vertexList.getPositionArray(),
				vertexList.getUVArray()
		);
		mesh.setAdditionalAttributeComponents(new int[] { 3 });
		mesh.setAdditionalAttributes(vertexList.getNormalArray());
		return mesh;
	}

	private void createWall(VertexList vertexList, float x0, float y0, float x1, float y1, float dx, float dy, boolean connect0, boolean connect1, boolean connect) {
		float u = 0;
		float v = 7;

		// wall
		if (!connect) {
			int index1 = vertexList.addVertex(x0, 0, y0, u, v, 1, 1, 1);
			int index2 = vertexList.addVertex(x1, 0, y1, u + 1, v, 1, 1, 1);
			int index3 = vertexList.addVertex(x1 + (dx + x0 - x1) * WALL_OFFSET, 1, y1 + (dy + y0 - y1) * WALL_OFFSET, u + (1 - WALL_OFFSET), v + 1, 1, 1, 1);
			int index4 = vertexList.addVertex(x0 + (dx + x1 - x0) * WALL_OFFSET, 1, y0 + (dy + y1 - y0) * WALL_OFFSET, u + (WALL_OFFSET), v + 1, 1, 1, 1);

			vertexList.addIndex(index1, index2, index3);
			vertexList.addIndex(index1, index3, index4);
		} else {

			// wall connection
			if (connect && connect0) {
				int index1 = vertexList.addVertex(x0, 0, y0, u + 1, v, 1, 1, 1);
				int index2 = vertexList.addVertex(x0 + (x1 - x0) * WALL_OFFSET, 1, y0 + (y1 - y0) * WALL_OFFSET, u + 1, v + 1, 1, 1, 1);
				int index3 = vertexList.addVertex(x0 + (dx + x1 - x0) * WALL_OFFSET, 1, y0 + (dy + y1 - y0) * WALL_OFFSET, u + (1 - WALL_OFFSET), v + 1, 1, 1, 1);

				vertexList.addIndex(index1, index2, index3);
			}
			if (connect && connect1) {
				int index4 = vertexList.addVertex(x1, 0, y1, u, v, 1, 1, 1);
				int index5 = vertexList.addVertex(x1 + (dx + x0 - x1) * WALL_OFFSET, 1, y1 + (dy + y0 - y1) * WALL_OFFSET, u + WALL_OFFSET, v + 1, 1, 1, 1);
				int index6 = vertexList.addVertex(x1 + (x0 - x1) * WALL_OFFSET, 1, y1 + (y0 - y1) * WALL_OFFSET, u, v + 1, 1, 1, 1);

				vertexList.addIndex(index4, index5, index6);
			}

			// ceiling (
			/*{
				int index1 = vertexList.addVertex(x0 + (x1 - x0) * WALL_OFFSET, 1, y0 + (y1 - y0) * WALL_OFFSET, 0, 0, 1, 1, 1);
				int index2 = vertexList.addVertex(x1 + (x0 - x1) * WALL_OFFSET, 1, y1 + (y0 - y1) * WALL_OFFSET, 1, 0, 1, 1, 1);
				int index3 = vertexList.addVertex(x1 + (dx + x0 - x1) * WALL_OFFSET, 1, y1 + (dy + y0 - y1) * WALL_OFFSET, 1, 1, 1, 1, 1);
				int index4 = vertexList.addVertex(x0 + (dx + x1 - x0) * WALL_OFFSET, 1, y0 + (dy + y1 - y0) * WALL_OFFSET, 0, 1, 1, 1, 1);

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
