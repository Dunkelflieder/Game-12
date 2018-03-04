package game12.client.controller.thirdPirson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.Camera;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.OrthographicCamera;
import de.nerogar.noise.render.deferredRenderer.Light;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Vector2f;
import de.nerogar.noise.util.Vector3f;
import game12.client.Controller;
import game12.client.gui.Gui;
import game12.client.gui.GuiContainer;
import game12.client.map.ClientMap;
import game12.client.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ThirdPersonController extends Controller {

	private static final float CAMERA_SPEED = 5f;
	private final Camera   camera;
	private final Vector2f cameraPosition;

	private Light light;

	public ThirdPersonController(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter,
			GuiContainer guiContainer) {
		super(window, eventManager, maps, networkAdapter, guiContainer);

		Gui gui = new ThirdPersonGui(eventManager);
		this.guiContainer.setActiveGui(gui);

		maps.get(0).setActive(true);

		cameraPosition = new Vector2f();
		camera = new OrthographicCamera(10, (float) window.getWidth() / window.getHeight(), 0f, -1000f);
		maps.get(0).getSystem(RenderSystem.class).setCamera(camera);

		//camera = maps.get(0).getSystem(RenderSystem.class).getCamera();

		camera.setXYZ(0, 10, 0);
		camera.setPitch((float) (-Math.PI / 2));

		light = new Light(
				new Vector3f(0, 1, 0),
				Color.WHITE,
				8,
				5
		);
		maps.get(0).getSystem(RenderSystem.class).getRenderer().getLightContainer().add(light);
	}

	@Override
	public void update(float timeDelta) {

		float deltaX = 0;
		float deltaY = 0;

		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_A)) deltaX -= 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_D)) deltaX += 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_W)) deltaY -= 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_S)) deltaY += 1;

		cameraPosition.addX(deltaX * CAMERA_SPEED * timeDelta);
		cameraPosition.addY(deltaY * CAMERA_SPEED * timeDelta);

		camera.setXYZ(cameraPosition.getX(), 10, cameraPosition.getY());

		light.position.set(cameraPosition.getX(), 0.5f, cameraPosition.getY());

	}

}
