package game12.client.controller.thirdPirson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.OrthographicCamera;
import de.nerogar.noise.util.Vector3f;
import game12.client.Controller;
import game12.client.gui.GuiContainer;
import game12.client.map.ClientMap;
import game12.client.systems.MapRenderSystem;
import game12.client.systems.RenderSystem;
import game12.core.systems.GameProgressSystem;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ThirdPersonController extends Controller {

	private static final float CAMERA_SPEED = 1f;

	private final ThirdPersonGui     gui;
	private final ClientMap          map;
	private final OrthographicCamera camera;
	private final Vector3f           cameraPosition;
	private float zoom = 10;

	private MapBuilder mapBuilder;

	private GameProgressSystem gameProgressSystem;

	public ThirdPersonController(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter,
			GuiContainer guiContainer) {
		super(window, eventManager, maps, networkAdapter, guiContainer);

		map = maps.get(0);
		map.setActive(true);

		this.mapBuilder = new MapBuilder(map);
		gui = new ThirdPersonGui(eventManager, mapBuilder);
		this.guiContainer.setActiveGui(gui);

		gameProgressSystem = map.getSystem(GameProgressSystem.class);

		map.getSystem(MapRenderSystem.class).setMarkRooms(true);

		cameraPosition = new Vector3f();
		camera = new OrthographicCamera(10, (float) window.getWidth() / window.getHeight(), 0f, -1000f);
		map.getSystem(RenderSystem.class).setCamera(camera);

		//camera = maps.get(0).getSystem(RenderSystem.class).getCamera();

		camera.setXYZ(0, 100, 0);
		camera.setPitch((float) (-Math.PI / 2));

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
		zoom = Math.max(4, Math.min(80, zoom));

		camera.setXYZ(cameraPosition.getX(), 100, cameraPosition.getY());
		camera.setHeight(zoom);

		// room building

		int mouseX = (int) ((inputHandler.getCursorPosX() / inputHandler.getWindow().getWidth() - 0.5f) * (zoom * camera.getAspect()) + cameraPosition.getX());
		int mouseY = (int) (-(inputHandler.getCursorPosY() / inputHandler.getWindow().getHeight() - 0.5f) * zoom + cameraPosition.getY());

		mapBuilder.update(inputHandler, mouseX, mouseY, zoom, camera, cameraPosition, gameProgressSystem.getCurrentRoom());

		gui.setCurrentRoom(gameProgressSystem.getCurrentRoom());
		gui.setTime(gameProgressSystem.getTime());

	}

}
