package game12.client.controller.thirdPirson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.OrthographicCamera;
import de.nerogar.noise.render.deferredRenderer.Light;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Vector2f;
import de.nerogar.noise.util.Vector3f;
import game12.client.Controller;
import game12.client.gui.GuiContainer;
import game12.client.map.ClientMap;
import game12.client.systems.RenderSystem;
import game12.core.request.MapChangeRequestPacket;
import game12.core.systems.GameProgressSystem;
import game12.core.systems.MapSystem;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ThirdPersonController extends Controller {

	private static final float CAMERA_SPEED = 1f;

	private final ThirdPersonGui     gui;
	private final ClientMap          map;
	private final OrthographicCamera camera;
	private final Vector2f           cameraPosition;
	private float zoom = 10;

	private Light light;

	private GameProgressSystem gameProgressSystem;

	public ThirdPersonController(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter,
			GuiContainer guiContainer) {
		super(window, eventManager, maps, networkAdapter, guiContainer);

		gui = new ThirdPersonGui(eventManager);
		this.guiContainer.setActiveGui(gui);

		map = maps.get(0);
		map.setActive(true);

		gameProgressSystem = map.getSystem(GameProgressSystem.class);

		cameraPosition = new Vector2f();
		camera = new OrthographicCamera(10, (float) window.getWidth() / window.getHeight(), 0f, -1000f);
		map.getSystem(RenderSystem.class).setCamera(camera);

		//camera = maps.get(0).getSystem(RenderSystem.class).getCamera();

		camera.setXYZ(0, 10, 0);
		camera.setPitch((float) (-Math.PI / 2));

		light = new Light(
				new Vector3f(0, 1, 0),
				Color.WHITE,
				8,
				5
		);
		map.getSystem(RenderSystem.class).getRenderer().getLightContainer().add(light);
	}

	@Override
	public void update(float timeDelta) {

		float deltaX = 0;
		float deltaY = 0;

		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_A)) deltaX -= 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_D)) deltaX += 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_W)) deltaY -= 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_S)) deltaY += 1;

		cameraPosition.addX(deltaX * CAMERA_SPEED * zoom * timeDelta);
		cameraPosition.addY(deltaY * CAMERA_SPEED * zoom * timeDelta);
		zoom -= inputHandler.getScrollDeltaY() * 2;
		zoom = Math.max(4, Math.min(40, zoom));

		camera.setXYZ(cameraPosition.getX(), 10, cameraPosition.getY());
		camera.setHeight(zoom);

		light.position.set(cameraPosition.getX(), 0.5f, cameraPosition.getY());

		// room building
		int mouseX = (int) ((inputHandler.getCursorPosX() / window.getWidth() - 0.5f) * (zoom * camera.getAspect()) + cameraPosition.getX());
		int mouseY = (int) (-(inputHandler.getCursorPosY() / window.getHeight() - 0.5f) * zoom + cameraPosition.getY());

		if (inputHandler.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			map.getNetworkAdapter().send(new MapChangeRequestPacket(mouseX, mouseY, gameProgressSystem.getCurrentRoom()));
		}

		if (inputHandler.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
			map.getNetworkAdapter().send(new MapChangeRequestPacket(mouseX, mouseY, MapSystem.VOID));
		}

		gui.setCurrentRoom(gameProgressSystem.getCurrentRoom());
		gui.setTime(gameProgressSystem.getTime());

	}

}
