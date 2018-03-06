package game12.client.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import game12.client.components.SpriteComponent;
import game12.client.map.ClientMap;
import game12.core.LogicSystem;
import game12.core.event.EntityMoveEvent;
import game12.core.event.UpdateEvent;

import java.util.HashMap;
import java.util.Map;

public class SpriteSystem extends LogicSystem {

	private ClientMap        map;
	private DeferredRenderer renderer;

	private Map<String, DeferredContainer> deferredContainerMap;

	private EventListener<EntityMoveEvent> moveListener;
	private EventListener<UpdateEvent>     updateListener;

	private Mesh      mesh;
	private Shader    shader;
	private Texture2D normal;
	private Texture2D light;

	public SpriteSystem(ClientMap map) {
		this.map = map;

		deferredContainerMap = new HashMap<>();
	}

	@Override
	public void init() {
		moveListener = this::moveListenerFunction;
		getEventManager().registerImmediate(EntityMoveEvent.class, moveListener);

		updateListener = this::updateListenerFunction;
		getEventManager().registerImmediate(UpdateEvent.class, updateListener);

		this.renderer = map.getSystem(RenderSystem.class).getRenderer();

		mesh = createMesh();
		shader = DeferredContainer.createSurfaceShader("shaders/sprite/sprite.vert", "shaders/sprite/sprite.frag", false);
		normal = Texture2DLoader.loadTexture("<normal.png>");
		light = Texture2DLoader.loadTexture("<red.png>");
	}

	public void registerEntity(SpriteComponent component) {
		renderer.addObject(component.getRenderable());
	}

	private void moveListenerFunction(EntityMoveEvent event) {
		SpriteComponent renderComponent = event.getEntity().getComponent(SpriteComponent.class);
		if (renderComponent != null) {
			renderComponent.updatePosition(event.getNewX(), event.getNewY(), event.getNewZ(), event.getNewScale());
		}
	}

	private void updateListenerFunction(UpdateEvent event) {
		Camera camera = map.getSystem(RenderSystem.class).getCamera();

		for (SpriteComponent spriteComponent : map.getEntityList().getComponents(SpriteComponent.class)) {
			spriteComponent.updateRotation(camera.getYaw(), -camera.getPitch(), camera.getRoll());
		}
	}

	public void unregisterEntity(SpriteComponent component) {
		renderer.removeObject(component.getRenderable());
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(EntityMoveEvent.class, moveListener);
		getEventManager().unregister(UpdateEvent.class, updateListener);
	}

	public DeferredContainer getDeferredContainer(String skinId) {
		return deferredContainerMap.computeIfAbsent(skinId, id -> {
			Texture2D color = Texture2DLoader.loadTexture("res/sprites/" + skinId + ".png", Texture2D.InterpolationType.NEAREST_MIPMAP);
			return new DeferredContainer(mesh, shader, color, normal, light, false, DeferredContainer.OptimizationStrategy.OPTIMIZATION_FEW);
		});
	}

	private Mesh createMesh() {
		VertexList vertexList = new VertexList();
		int index1 = vertexList.addVertex(-0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		int index2 = vertexList.addVertex(0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		int index3 = vertexList.addVertex(0.5f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
		int index4 = vertexList.addVertex(-0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);

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

}
