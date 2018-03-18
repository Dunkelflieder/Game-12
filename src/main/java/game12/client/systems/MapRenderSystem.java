package game12.client.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderable;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import de.nerogar.noise.util.Color;
import game12.client.event.BeforeRenderEvent;
import game12.core.LogicSystem;
import game12.core.event.MapChangeEvent;
import game12.core.systems.GameProgressSystem;
import game12.core.systems.MapSystem;

public class MapRenderSystem extends LogicSystem {

	private class TileTexture {

		private float u;
		private float v;

		public TileTexture(float u, float v) {
			this.u = u;
			this.v = v;
		}
	}

	private static final float WALL_OFFSET = 0.3f;
	private static final int   TILE_COUNT  = 8;
	private static final float TILE_SIZE   = 1f / TILE_COUNT;

	private final Color COLOR_CURRENT_ROOM = new Color(0.4f, 1.0f, 0.1f, 1.0f);
	private final Color COLOR_LOCKED_ROOM  = new Color(1.0f, 0.2f, 0.0f, 1.0f);
	private boolean markRooms;

	private EventListener<MapChangeEvent>    mapChangeEventListener;
	private EventListener<BeforeRenderEvent> beforeRenderEventListener;

	private boolean shouldRecreate = true;

	private Texture2D white         = Texture2DLoader.loadTexture("<white.png>", Texture2D.InterpolationType.NEAREST_MIPMAP).setAnisotropicFiltering(8f);
	private Texture2D red           = Texture2DLoader.loadTexture("<red.png>");
	private Texture2D defaultNormal = Texture2DLoader.loadTexture("<normal.png>");

	private Texture2D color      = Texture2DLoader.loadTexture("res/map/color.png", Texture2D.InterpolationType.NEAREST_MIPMAP).setAnisotropicFiltering(8f);
	private Texture2D background = Texture2DLoader.loadTexture("res/background/background.png", Texture2D.InterpolationType.NEAREST_MIPMAP).setAnisotropicFiltering(8f);
	private Texture2D normal     = Texture2DLoader.loadTexture("res/map/normal.png", Texture2D.InterpolationType.NEAREST_MIPMAP).setAnisotropicFiltering(8f);

	private Shader mapShader = DeferredContainer.createSurfaceShader("shaders/map/map.vert", "shaders/map/map.frag", true);

	private DeferredRenderer   renderer;
	private DeferredRenderable mapRenderable;
	private DeferredRenderable backgroundRenderable;

	private MapSystem          mapSystem;
	private GameProgressSystem gameProgressSystem;

	private TileTexture[] tileTextures = {
			new TileTexture(1, 7),
			new TileTexture(2, 7),
			new TileTexture(1, 7)
	};

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
		gameProgressSystem = getContainer().getSystem(GameProgressSystem.class);

		addBackgroundMesh();
	}

	public void setMarkRooms(boolean markRooms) {
		this.markRooms = markRooms;
		shouldRecreate = true;
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

	private void addBackgroundMesh() {
		Mesh backgroundMesh = createBackgroundMesh(mapSystem.getWidth(), mapSystem.getHeight());

		DeferredContainer container = new DeferredContainer(backgroundMesh, null, background, defaultNormal, red);
		backgroundRenderable = new DeferredRenderable(container, new RenderProperties3f());

		renderer.addObject(backgroundRenderable);

	}

	private Mesh createBackgroundMesh(int width, int height) {
		VertexList vertexList = new VertexList();

		float yOffset = -100f;

		int index1 = vertexList.addVertex(0.0f, yOffset, height, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		int index2 = vertexList.addVertex(width, yOffset, height, width, 0.0f, 0.0f, 1.0f, 0.0f);
		int index3 = vertexList.addVertex(width, yOffset, 0.0f, width, height, 0.0f, 1.0f, 0.0f);
		int index4 = vertexList.addVertex(0.0f, yOffset, 0.0f, 0.0f, height, 0.0f, 1.0f, 0.0f);

		vertexList.addIndex(index1, index2, index3);
		vertexList.addIndex(index1, index3, index4);

		return new Mesh(
				vertexList.getIndexCount(),
				vertexList.getVertexCount(),
				vertexList.getIndexArray(),
				vertexList.getPositionArray(),
				vertexList.getUVArray(),
				vertexList.getNormalArray()
		);
	}

	private Mesh createMesh() {
		VertexList vertexList = new VertexList();

		for (int x = 0; x < mapSystem.getWidth(); x++) {
			for (int y = 0; y < mapSystem.getHeight(); y++) {
				int room = mapSystem.get(x, y);
				int tile = mapSystem.getTile(x, y);

				Color color = Color.BLACK;

				if (markRooms) {
					if (room == gameProgressSystem.getCurrentRoom()) {
						color = COLOR_CURRENT_ROOM;
					} else if (mapSystem.isRoomLocked(room)) {
						color = COLOR_LOCKED_ROOM;
					}
				}

				if (room == MapSystem.VOID) {
					createWall(vertexList, x + 0, y + 0, x + 0, y + 1, 1, 0,
					           mapSystem.get(x - 1, y - 1) > MapSystem.VOID || mapSystem.get(x, y - 1) > MapSystem.VOID,
					           mapSystem.get(x - 1, y + 1) > MapSystem.VOID || mapSystem.get(x, y + 1) > MapSystem.VOID,
					           mapSystem.get(x - 1, y) <= MapSystem.VOID,
					           mapSystem.get(x - 1, y) == MapSystem.DOOR || mapSystem.get(x - 1, y) == MapSystem.LOCKED_DOOR
					          );
					createWall(vertexList, x + 0, y + 1, x + 1, y + 1, 0, -1,
					           mapSystem.get(x - 1, y + 1) > MapSystem.VOID || mapSystem.get(x - 1, y) > MapSystem.VOID,
					           mapSystem.get(x + 1, y + 1) > MapSystem.VOID || mapSystem.get(x + 1, y) > MapSystem.VOID,
					           mapSystem.get(x, y + 1) <= 0,
					           mapSystem.get(x, y + 1) == MapSystem.DOOR || mapSystem.get(x, y + 1) == MapSystem.LOCKED_DOOR
					          );
					createWall(vertexList, x + 1, y + 1, x + 1, y + 0, -1, 0,
					           mapSystem.get(x + 1, y + 1) > MapSystem.VOID || mapSystem.get(x, y + 1) > MapSystem.VOID,
					           mapSystem.get(x + 1, y - 1) > MapSystem.VOID || mapSystem.get(x, y - 1) > MapSystem.VOID,
					           mapSystem.get(x + 1, y) <= 0,
					           mapSystem.get(x + 1, y) == MapSystem.DOOR || mapSystem.get(x + 1, y) == MapSystem.LOCKED_DOOR
					          );
					createWall(vertexList, x + 1, y + 0, x + 0, y + 0, 0, 1,
					           mapSystem.get(x + 1, y - 1) > MapSystem.VOID || mapSystem.get(x + 1, y) > MapSystem.VOID,
					           mapSystem.get(x - 1, y - 1) > MapSystem.VOID || mapSystem.get(x - 1, y) > MapSystem.VOID,
					           mapSystem.get(x, y - 1) <= 0,
					           mapSystem.get(x, y - 1) == MapSystem.DOOR || mapSystem.get(x, y - 1) == MapSystem.LOCKED_DOOR
					          );
				} else {
					float u = tileTextures[tile].u;
					float v = tileTextures[tile].v;

					int indexNN = vertexList.addVertex(x + 0, 0, y + 0, u, v + 1, color.getR(), color.getG(), color.getB());
					int indexPN = vertexList.addVertex(x + 1, 0, y + 0, u + 1, v + 1, color.getR(), color.getG(), color.getB());
					int indexPP = vertexList.addVertex(x + 1, 0, y + 1, u + 1, v, color.getR(), color.getG(), color.getB());
					int indexNP = vertexList.addVertex(x + 0, 0, y + 1, u, v, color.getR(), color.getG(), color.getB());

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

	private void createWall(VertexList vertexList, float x0, float y0, float x1, float y1, float dx, float dy, boolean connect0, boolean connect1, boolean connect, boolean door) {
		float u = 0;
		float v = 7;

		// wall
		if (!connect) {
			int index1 = vertexList.addVertex(x0, 0, y0, u, v, 0, 0, 0);
			int index2 = vertexList.addVertex(x1, 0, y1, u + 1, v, 0, 0, 0);
			int index3 = vertexList.addVertex(x1 + (dx + x0 - x1) * WALL_OFFSET, 1, y1 + (dy + y0 - y1) * WALL_OFFSET, u + (1 - WALL_OFFSET), v + 1, 0, 0, 0);
			int index4 = vertexList.addVertex(x0 + (dx + x1 - x0) * WALL_OFFSET, 1, y0 + (dy + y1 - y0) * WALL_OFFSET, u + (WALL_OFFSET), v + 1, 0, 0, 0);

			vertexList.addIndex(index1, index2, index3);
			vertexList.addIndex(index1, index3, index4);

		} else {

			// wall connection
			if (connect && connect0) {
				int index1 = vertexList.addVertex(x0, 0, y0, u + 1, v, 0, 0, 0);
				int index2 = vertexList.addVertex(x0 + (x1 - x0) * WALL_OFFSET, 1, y0 + (y1 - y0) * WALL_OFFSET, u + 1, v + 1, 0, 0, 0);
				int index3 = vertexList.addVertex(x0 + (dx + x1 - x0) * WALL_OFFSET, 1, y0 + (dy + y1 - y0) * WALL_OFFSET, u + (1 - WALL_OFFSET), v + 1, 0, 0, 0);

				vertexList.addIndex(index1, index2, index3);
			}

			if (connect && connect1) {
				int index4 = vertexList.addVertex(x1, 0, y1, u, v, 0, 0, 0);
				int index5 = vertexList.addVertex(x1 + (dx + x0 - x1) * WALL_OFFSET, 1, y1 + (dy + y0 - y1) * WALL_OFFSET, u + WALL_OFFSET, v + 1, 0, 0, 0);
				int index6 = vertexList.addVertex(x1 + (x0 - x1) * WALL_OFFSET, 1, y1 + (y0 - y1) * WALL_OFFSET, u, v + 1, 0, 0, 0);

				vertexList.addIndex(index4, index5, index6);
			}

			if (door) {
				int index1 = vertexList.addVertex(x0, 0, y0, u, v, 0, 0, 0);
				int index2 = vertexList.addVertex(x1, 0, y1, u + 1, v, 0, 0, 0);
				int index3 = vertexList.addVertex(x1 + (x0 - x1) * WALL_OFFSET, 1, y1 + (y0 - y1) * WALL_OFFSET, u + (1 - WALL_OFFSET), v + 1, 0, 0, 0);
				int index4 = vertexList.addVertex(x0 + (x1 - x0) * WALL_OFFSET, 1, y0 + (y1 - y0) * WALL_OFFSET, u + (WALL_OFFSET), v + 1, 0, 0, 0);

				vertexList.addIndex(index1, index2, index3);
				vertexList.addIndex(index1, index3, index4);
			}

		}

	}

	@Override
	public void cleanup() {
		getEventManager().unregister(MapChangeEvent.class, mapChangeEventListener);
		getEventManager().unregister(BeforeRenderEvent.class, beforeRenderEventListener);
	}
}
