package game12.client.controller.thirdPirson;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.input.MouseButtonEvent;
import de.nerogar.noise.render.Camera;
import de.nerogar.noise.util.Vector3f;
import game12.client.map.ClientMap;
import game12.core.request.EntityPlaceRequestPacket;
import game12.core.request.MapChangeRequestPacket;
import game12.core.request.MapTileChangeRequestPacket;
import game12.core.systems.GameObjectsSystem;
import game12.core.systems.MapSystem;
import org.lwjgl.glfw.GLFW;

public class MapBuilder {

	private ClientMap map;

	private enum BuildType {
		ROOM(false, 0),
		LAVA(false, 0),
		DOOR(false, 0),
		SPIDER(true, 1),
		TURRET(true, 2),
		SPIDER_BOSS(true, 15),
		SPIKE_TRAP(true, 2);

		private boolean isEntity;
		private short   blueprintId;
		private int     cost;

		BuildType(boolean isEntity, int cost) {
			this.isEntity = isEntity;
			this.cost = cost;
		}
	}

	private BuildType buildType;

	public MapBuilder(ClientMap map) {
		this.map = map;

		BuildType.SPIDER.blueprintId = map.getGameSystem(GameObjectsSystem.class).getID("spider");
		BuildType.TURRET.blueprintId = map.getGameSystem(GameObjectsSystem.class).getID("turret");
		BuildType.SPIDER_BOSS.blueprintId = map.getGameSystem(GameObjectsSystem.class).getID("spiderBoss");
		BuildType.SPIKE_TRAP.blueprintId = map.getGameSystem(GameObjectsSystem.class).getID("spikeTrap");
	}

	public void update(InputHandler inputHandler, int mouseX, int mouseY, float zoom, Camera camera, Vector3f cameraPosition, int currentRoom) {
		if (buildType == null) return;

		if (buildType.isEntity) {
			for (MouseButtonEvent mouseButtonEvent : inputHandler.getMouseButtonEvents()) {
				if (mouseButtonEvent.action == GLFW.GLFW_PRESS && mouseButtonEvent.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					map.getNetworkAdapter().send(new EntityPlaceRequestPacket(mouseX, mouseY, buildType.blueprintId, buildType.cost));
					mouseButtonEvent.setProcessed();
				}
			}
		} else {
			if (inputHandler.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
				switch (buildType) {
					case ROOM:
						map.getNetworkAdapter().send(new MapChangeRequestPacket(mouseX, mouseY, currentRoom));
						break;
					case LAVA:
						map.getNetworkAdapter().send(new MapTileChangeRequestPacket(mouseX, mouseY, MapSystem.TILE_LAVA));
						break;
					case DOOR:
						map.getNetworkAdapter().send(new MapChangeRequestPacket(mouseX, mouseY, MapSystem.DOOR));
						break;
				}
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

	public void spiderBossButton() {
		buildType = BuildType.SPIDER_BOSS;
	}

	public void spikeTrapButton() {
		buildType = BuildType.SPIKE_TRAP;
	}

	public void lavaButton() {
		buildType = BuildType.LAVA;
	}
}


