package game12.client.controller.firstPerson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.Camera;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.util.Vector2f;
import game12.client.Controller;
import game12.client.gui.Gui;
import game12.client.gui.GuiContainer;
import game12.client.map.ClientMap;
import game12.client.systems.RenderSystem;

import java.util.List;

public class FirstPersonController extends Controller {

	private static final float CAMERA_SPEED = 5f;
	private final Camera   camera;
	private final Vector2f cameraPosition;
	private       float    yaw;

	public FirstPersonController(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter,
			GuiContainer guiContainer) {
		super(window, eventManager, maps, networkAdapter, guiContainer);

		Gui gui = new FirstPersonGui(eventManager);
		this.guiContainer.setActiveGui(gui);

		maps.get(0).setActive(true);

		cameraPosition = new Vector2f();
		camera = maps.get(0).getSystem(RenderSystem.class).getCamera();

		camera.setXYZ(0, 0.5f, 0);
	}

	@Override
	public void update(float timeDelta) {
		inputHandler.setMouseHiding(true);

		yaw += inputHandler.getCursorDeltaX() * -0.003f;
		camera.setYaw(yaw);
	}

}
