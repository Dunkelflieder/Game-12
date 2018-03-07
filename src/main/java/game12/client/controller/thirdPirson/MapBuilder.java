package game12.client.controller.thirdPirson;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.render.Camera;
import de.nerogar.noise.util.Vector3f;
import game12.client.map.ClientMap;
import game12.core.request.MapChangeRequestPacket;
import game12.core.systems.MapSystem;
import org.lwjgl.glfw.GLFW;

public class MapBuilder {

	private ClientMap      map;

	private enum BuildType {
		ROOM,
		DOOR,
	}

	private BuildType buildType = BuildType.ROOM;

	public MapBuilder(ClientMap map) {
		this.map = map;
	}

	public void update(InputHandler inputHandler, float zoom, Camera camera, Vector3f cameraPosition, int currentRoom) {
		int mouseX = (int) ((inputHandler.getCursorPosX() / inputHandler.getWindow().getWidth() - 0.5f) * (zoom * camera.getAspect()) + cameraPosition.getX());
		int mouseY = (int) (-(inputHandler.getCursorPosY() / inputHandler.getWindow().getHeight() - 0.5f) * zoom + cameraPosition.getY());

		if (inputHandler.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			if (buildType == BuildType.ROOM) {
				map.getNetworkAdapter().send(new MapChangeRequestPacket(mouseX, mouseY, currentRoom));
			} else if (buildType == BuildType.DOOR) {
				map.getNetworkAdapter().send(new MapChangeRequestPacket(mouseX, mouseY, MapSystem.DOOR));
			}

		}

	}

	public void roomButton() {
		buildType = BuildType.ROOM;
	}

	public void doorButton() {
		buildType = BuildType.DOOR;
	}

}
