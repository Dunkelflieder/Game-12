package game12.client.controller.thirdPirson;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.render.Camera;
import de.nerogar.noise.util.Vector3f;
import game12.client.map.ClientMap;
import game12.core.request.EntityPlaceRequestPacket;
import game12.core.request.MapChangeRequestPacket;
import game12.core.systems.GameObjectsSystem;
import game12.core.systems.MapSystem;
import org.lwjgl.glfw.GLFW;

public class MapBuilder {

	private ClientMap map;

	private enum BuildType {
		ROOM,
		DOOR,
		SPIDER,
		TURRET,;

		private short blueprintId;
	}

	private BuildType buildType;

	public MapBuilder(ClientMap map) {
		this.map = map;

		BuildType.SPIDER.blueprintId = map.getGameSystem(GameObjectsSystem.class).getID("spider");
		BuildType.TURRET.blueprintId = map.getGameSystem(GameObjectsSystem.class).getID("turret");
	}

	public void update(InputHandler inputHandler, int mouseX, int mouseY, float zoom, Camera camera, Vector3f cameraPosition, int currentRoom) {
		if (inputHandler.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			if (buildType == BuildType.ROOM) {
				map.getNetworkAdapter().send(new MapChangeRequestPacket(mouseX, mouseY, currentRoom));
			} else if (buildType == BuildType.DOOR) {
				map.getNetworkAdapter().send(new MapChangeRequestPacket(mouseX, mouseY, MapSystem.DOOR));
			} else if (buildType == BuildType.SPIDER) {
				map.getNetworkAdapter().send(new EntityPlaceRequestPacket(mouseX, mouseY, buildType.blueprintId));
			} else if (buildType == BuildType.TURRET) {
				map.getNetworkAdapter().send(new EntityPlaceRequestPacket(mouseX, mouseY, buildType.blueprintId));
			}

		}

	}

	public void noButton() {
		buildType = null;
	}

	public void roomButton() {
		buildType = BuildType.ROOM;
	}

	public void doorButton() {
		buildType = BuildType.DOOR;
	}

	public void spiderButton() {
		buildType = BuildType.SPIDER;
	}

	public void turretButton() {
		buildType = BuildType.TURRET;
	}

}


