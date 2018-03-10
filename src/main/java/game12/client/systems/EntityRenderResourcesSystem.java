package game12.client.systems;

import de.nerogar.noise.render.Mesh;
import de.nerogar.noise.render.Texture2D;
import de.nerogar.noise.render.Texture2DLoader;
import de.nerogar.noise.render.WavefrontLoader;
import de.nerogar.noise.render.deferredRenderer.DeferredContainer;
import game12.client.components.RenderComponent;
import game12.core.LogicSystem;
import game12.core.systems.GameObjectsSystem;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Queue;

public class EntityRenderResourcesSystem extends LogicSystem {

	private class ResourceContainer {

		private Mesh              mesh;
		private Texture2D         colorTexture;
		private Texture2D         normalTexture;
		private Texture2D         lightTexture;
		private DeferredContainer deferredContainer;

		public ResourceContainer(Mesh mesh, Texture2D colorTexture, Texture2D normalTexture, Texture2D lightTexture) {
			this.mesh = mesh;
			this.colorTexture = colorTexture;
			this.normalTexture = normalTexture;
			this.lightTexture = lightTexture;
			this.deferredContainer = new DeferredContainer(mesh, null, colorTexture, normalTexture, lightTexture);
		}

	}

	private static Texture2D defaultColorTexture;
	private static Texture2D defaultNormalTexture;
	private static Texture2D defaultLightTexture;

	private GameObjectsSystem gameObjectsSystem;

	private HashMap<String, ResourceContainer> resources;
	private ResourceContainer                  missingGameObjectResources;

	private Queue<String> skins;
	private boolean       doneLoading;

	public EntityRenderResourcesSystem() {
		resources = new HashMap<>();
		skins = new ArrayDeque<>();
	}

	@Override
	public void init() {
		gameObjectsSystem = getContainer().getSystem(GameObjectsSystem.class);

		startInit();
		while (!isDoneLoading()) {
			initStep();
		}
	}

	@Override
	public void initWithData() {
	}

	public void startInit() {
		// TODO add missing object
		missingGameObjectResources = null;
		/*new ResourceContainer(
				WavefrontLoader.loadObject("res/objects/test2x2/mesh.obj"),
				Texture2DLoader.loadTexture("res/objects/test2x2/color.png"),
				Texture2DLoader.loadTexture("res/objects/test2x2/normal.png"),
				Texture2DLoader.loadTexture("res/objects/test2x2/light.png")
		);
		*/

		defaultColorTexture = Texture2DLoader.loadTexture("<white.png>");
		defaultNormalTexture = Texture2DLoader.loadTexture("<normal.png>");
		defaultLightTexture = Texture2DLoader.loadTexture("<red.png>");

		GameObjectsSystem gameObjectsSystem = getContainer().getSystem(GameObjectsSystem.class);

		for (short entityID : gameObjectsSystem.objectIDs) {
			if (gameObjectsSystem.getComponentForEntity(entityID, RenderComponent.class) != null) {
				skins.addAll(Arrays.asList(gameObjectsSystem.getComponentForEntity(entityID, RenderComponent.class).getSkinIDs()));
			}
		}

	}

	public void initStep() {
		if (doneLoading) return;

		boolean loaded = false;

		while (!loaded && !skins.isEmpty()) {
			loaded = addObject(skins.remove());
		}

		if (skins.isEmpty()) doneLoading = true;
	}

	public boolean isDoneLoading() {
		return doneLoading;
	}

	private boolean addObject(String name) {
		if (resources.containsKey(name)) return false;

		Texture2D colorTexture;
		Texture2D normalTexture;
		Texture2D lightTexture;

		if (new File("res/objects/" + name + "/color.png").exists()) {
			colorTexture = Texture2DLoader.loadTexture("res/objects/" + name + "/color.png", Texture2D.InterpolationType.NEAREST);
		} else {
			colorTexture = defaultColorTexture;
		}

		if (new File("res/objects/" + name + "/normal.png").exists()) {
			normalTexture = Texture2DLoader.loadTexture("res/objects/" + name + "/normal.png", Texture2D.InterpolationType.NEAREST);
		} else {
			normalTexture = defaultNormalTexture;
		}

		if (new File("res/objects/" + name + "/light.png").exists()) {
			lightTexture = Texture2DLoader.loadTexture("res/objects/" + name + "/light.png", Texture2D.InterpolationType.NEAREST);
		} else {
			lightTexture = defaultLightTexture;
		}

		resources.put(name, new ResourceContainer(
				WavefrontLoader.loadObject("res/objects/" + name + "/mesh.obj"),
				colorTexture, normalTexture, lightTexture
		));

		return true;
	}

	private ResourceContainer getResourceContainer(String id) {
		ResourceContainer container = resources.get(id);
		if (container == null) {
			container = missingGameObjectResources;
		}
		return container;
	}

	public DeferredContainer getDeferredContainer(String id) {
		return getResourceContainer(id).deferredContainer;
	}

	@Override
	public void cleanup() {
	}

}
