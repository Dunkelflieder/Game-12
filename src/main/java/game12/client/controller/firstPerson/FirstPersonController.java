package game12.client.controller.firstPerson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.input.MouseButtonEvent;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.Camera;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.util.Vector2f;
import de.nerogar.noise.util.Vector3f;
import game12.client.Controller;
import game12.client.gui.Gui;
import game12.client.gui.GuiContainer;
import game12.client.map.ClientMap;
import game12.client.systems.RenderSystem;
import game12.core.request.PlayerPositionUpdateRequestPacket;
import game12.core.request.ShootRequestPacket;
import game12.core.systems.MapSystem;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class FirstPersonController extends Controller {

	private static final float CAMERA_SPEED = 3f;
	private ClientMap map;

	private final Camera   camera;
	private final Vector2f cameraPosition;
	private       float    yaw;

	private final MapSystem mapSystem;

	public FirstPersonController(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter,
			GuiContainer guiContainer) {
		super(window, eventManager, maps, networkAdapter, guiContainer);

		this.map = maps.get(0);
		this.mapSystem = map.getSystem(MapSystem.class);

		map.getSystem(RenderSystem.class).setResolution(4);

		Gui gui = new FirstPersonGui(eventManager);
		this.guiContainer.setActiveGui(gui);

		map.setActive(true);

		cameraPosition = new Vector2f(2f, 2f);
		yaw = (float) (Math.PI * 1.5);
		camera = map.getSystem(RenderSystem.class).getCamera();
	}

	@Override
	public void update(float timeDelta) {
		inputHandler.setMouseHiding(true);

		yaw += inputHandler.getCursorDeltaX() * -0.015f;

		float deltaXlocal = 0;
		float deltaYlocal = 0;

		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_A)) deltaXlocal -= 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_D)) deltaXlocal += 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_W)) deltaYlocal -= 1;
		if (inputHandler.isKeyDown(GLFW.GLFW_KEY_S)) deltaYlocal += 1;

		float norm = (float) Math.sqrt(deltaXlocal * deltaXlocal + deltaYlocal * deltaYlocal);

		if (norm > 0) {
			deltaXlocal /= norm;
			deltaYlocal /= norm;
		}

		deltaXlocal *= CAMERA_SPEED * timeDelta;
		deltaYlocal *= CAMERA_SPEED * timeDelta;

		float deltaX = (float) (deltaXlocal * Math.cos(yaw) + deltaYlocal * Math.sin(yaw));
		float deltaY = (float) (deltaXlocal * Math.sin(-yaw) + deltaYlocal * Math.cos(-yaw));

		if (mapSystem.get((int) (cameraPosition.getX() + deltaX), (int) (cameraPosition.getY())) != MapSystem.VOID) {
			cameraPosition.addX(deltaX);
		}
		if (mapSystem.get((int) (cameraPosition.getX()), (int) (cameraPosition.getY() + deltaY)) != MapSystem.VOID) {
			cameraPosition.addY(deltaY);
		}

		camera.setXYZ(cameraPosition.getX(), 0.5f, cameraPosition.getY());
		camera.setYaw(yaw);

		Vector3f camPos = new Vector3f(
				camera.getX(),
				camera.getY(),
				camera.getZ()
		);
		map.makeRequest(PlayerPositionUpdateRequestPacket.of(camPos));

		for (MouseButtonEvent event : inputHandler.getMouseButtonEvents()) {
			if (event.action == GLFW.GLFW_PRESS && event.button == 0) {
				map.makeRequest(new ShootRequestPacket(ShootRequestPacket.TYPE_SHOTGUN, camPos, camera.getDirectionAt()));
			}
		}
	}

}
