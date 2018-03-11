package game12.client.systems;

import de.nerogar.noise.render.*;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import de.nerogar.noise.util.Vector3f;
import game12.client.components.SpriteComponent;
import game12.client.event.BeforeRenderEvent;
import game12.client.map.ClientMap;
import game12.core.LogicSystem;
import game12.core.components.PositionComponent;
import game12.core.event.EntityMoveEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SpriteSystem extends LogicSystem {

	private ClientMap        map;
	private DeferredRenderer renderer;

	private Map<String, DeferredContainer> deferredContainerMap;

	private Mesh      mesh;
	private Shader    shader;
	private Texture2D defaultNormal;
	private Texture2D light;

	public SpriteSystem(ClientMap map) {
		this.map = map;

		deferredContainerMap = new HashMap<>();
	}

	@Override
	public void init() {
		getEventManager().registerImmediate(EntityMoveEvent.class, this::moveListenerFunction);
		getEventManager().register(BeforeRenderEvent.class, this::beforeRenderListenerFunction);

		this.renderer = map.getSystem(RenderSystem.class).getRenderer();

		mesh = createMesh();
		shader = DeferredContainer.createSurfaceShader("shaders/sprite/sprite.vert", "shaders/sprite/sprite.frag", false);
		defaultNormal = Texture2DLoader.loadTexture("<normal.png>");
		light = Texture2DLoader.loadTexture("<red.png>");
	}

	public void registerEntity(SpriteComponent component) {
		PositionComponent positionComponent = component.getEntity().getComponent(PositionComponent.class);
		setSpritePosition(component, positionComponent.getX(), positionComponent.getY(), positionComponent.getZ(), positionComponent.getScale());
		renderer.addObject(component.getRenderable());
	}

	private void setSpritePosition(SpriteComponent spriteComponent, float x, float y, float z, float scale) {
		spriteComponent.updatePosition(x, y, z, scale);
	}

	private void moveListenerFunction(EntityMoveEvent event) {
		SpriteComponent spriteComponent = event.getEntity().getComponent(SpriteComponent.class);
		if (spriteComponent != null) {
			setSpritePosition(spriteComponent, event.getNewX(), event.getNewY(), event.getNewZ(), event.getNewScale());
		}
	}

	private void beforeRenderListenerFunction(BeforeRenderEvent event) {
		Camera camera = map.getSystem(RenderSystem.class).getCamera();

		for (SpriteComponent spriteComponent : map.getEntityList().getComponents(SpriteComponent.class)) {

			Vector3f forcedRotation = spriteComponent.getForcedRotation();
			if (forcedRotation == null) {
				spriteComponent.updateRotation(camera.getYaw(), -camera.getPitch(), camera.getRoll());
			} else {
				spriteComponent.updateRotation(forcedRotation.getX(), forcedRotation.getY(), forcedRotation.getZ());
			}
		}
	}

	public void unregisterEntity(SpriteComponent component) {
		renderer.removeObject(component.getRenderable());
	}

	public DeferredContainer getDeferredContainer(String skinId) {
		return deferredContainerMap.computeIfAbsent(skinId, id -> {
			Texture2D color = Texture2DLoader.loadTexture("res/sprites/" + skinId + "/color.png", Texture2D.InterpolationType.NEAREST_MIPMAP);
			Texture2D normal;
			if (new File("res/sprites/" + skinId + "/normal.png").exists()) {
				normal = Texture2DLoader.loadTexture("res/sprites/" + skinId + "/normal.png", Texture2D.InterpolationType.NEAREST_MIPMAP);
			} else {
				normal = defaultNormal;
			}
			return new DeferredContainer(mesh, shader, color, normal, light, false, DeferredContainer.OptimizationStrategy.OPTIMIZATION_FEW);
		});
	}

	private Mesh createMesh() {
		VertexList vertexList = new VertexList();
		int index1 = vertexList.addVertex(-0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		int index2 = vertexList.addVertex(0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		int index3 = vertexList.addVertex(0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
		int index4 = vertexList.addVertex(-0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);

		int index5 = vertexList.addVertex(0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		int index6 = vertexList.addVertex(-0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);
		int index7 = vertexList.addVertex(-0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
		int index8 = vertexList.addVertex(0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f);

		vertexList.addIndex(index1, index2, index3);
		vertexList.addIndex(index1, index3, index4);
		vertexList.addIndex(index5, index6, index7);
		vertexList.addIndex(index5, index7, index8);

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
