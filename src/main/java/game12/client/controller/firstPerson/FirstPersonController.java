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
import game12.client.systems.SoundSystem;
import game12.core.components.PositionComponent;
import game12.core.map.Entity;
import game12.core.networkEvents.EntityJumpEvent;
import game12.core.request.PlayerPositionUpdateRequestPacket;
import game12.core.request.ShootRequestPacket;
import game12.core.systems.GameObjectsSystem;
import game12.core.systems.MapSystem;
import game12.core.systems.PlayerSystem;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Random;

public class FirstPersonController extends Controller {

	private static final float  CAMERA_SPEED           = 3f;
	private static final double SCREEN_SHAKE_FACTOR    = 0.1f;
	private static final float  KNOCKBACK_DECELERATION = 30f;
	private static final float  KNOCKBACK              = 5f;
	private static final Random RANDOM                 = new Random();

	private ClientMap map;

	private final Camera   camera;
	private final Vector2f cameraPosition;
	private       float    yaw;

	private final MapSystem   mapSystem;
	private final SoundSystem soundSystem;

	private float screenShake;
	private Vector2f knockback = new Vector2f();

	public FirstPersonController(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter,
			GuiContainer guiContainer) {
		super(window, eventManager, maps, networkAdapter, guiContainer);

		this.map = maps.get(0);
		this.mapSystem = map.getSystem(MapSystem.class);

		//map.getSystem(RenderSystem.class).setResolution(480, 270);
		map.getSystem(RenderSystem.class).setResolution(640, 360);

		Gui gui = new FirstPersonGui(eventManager, map);
		this.guiContainer.setActiveGui(gui);

		map.setActive(true);

		cameraPosition = new Vector2f(2f, 2f);
		yaw = (float) (Math.PI * 1.5);
		camera = map.getSystem(RenderSystem.class).getCamera();

		inputHandler.setMouseHiding(true);

		soundSystem = map.getSystem(SoundSystem.class);

		// TODO: move this to another class
		map.getEventManager().register(EntityJumpEvent.class, event -> {
			if (event.getEventType() == EntityJumpEvent.IMPACT) {
				Entity entity = map.getEntityList().get(event.getEntity());

				if (entity.getEntityID() == map.getGameSystem(GameObjectsSystem.class).getID("spiderBoss")) {
					PositionComponent position = entity.getComponent(PositionComponent.class);
					map.getSystem(SoundSystem.class).playSound("res/sound/spiderBoss/impact.ogg", position.getX(), position.getY(), position.getZ());

					screenShake += 1.0;
				}

			}
		});

		PlayerSystem playerSystem = map.getSystem(PlayerSystem.class);
		playerSystem.hitEvent.register(event -> {
			knockback.setX(event.direction.getX());
			knockback.setY(event.direction.getZ());
			if (knockback.getSquaredValue() == 0) {
				// prevent NaN
				knockback.setX(RANDOM.nextFloat() - 0.5f);
				knockback.setY(RANDOM.nextFloat() - 0.5f);
			}
			knockback.setValue(KNOCKBACK);
			screenShake += 1;
			Vector3f playerPosition = playerSystem.getPlayerPosition();
			soundSystem.playSound("res/sound/player/hurt.ogg", playerPosition.getX(), playerPosition.getY(), playerPosition.getZ());
		});
	}

	@Override
	public void update(float timeDelta) {
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

		if (knockback.getSquaredValue() > 0.01) {
			deltaX += knockback.getX() * timeDelta;
			deltaY += knockback.getY() * timeDelta;
			float decelerationDelta = KNOCKBACK_DECELERATION * timeDelta;
			if (decelerationDelta >= knockback.getValue()) {
				knockback.set(0);
			} else {
				knockback.setValue(knockback.getValue() - decelerationDelta);
			}
		}

		if (mapSystem.isWalkable((int) (cameraPosition.getX() + deltaX), (int) cameraPosition.getY(), true)) {
			cameraPosition.addX(deltaX);
		}
		if (mapSystem.isWalkable((int) cameraPosition.getX(), (int) (cameraPosition.getY() + deltaY), true)) {
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

		// screen shake

		float screenShakeX = (float) (Math.random() * screenShake * screenShake * SCREEN_SHAKE_FACTOR);
		float screenShakeY = (float) (Math.random() * screenShake * screenShake * SCREEN_SHAKE_FACTOR);
		float screenShakeZ = (float) (Math.random() * screenShake * screenShake * SCREEN_SHAKE_FACTOR);

		camera.setXYZ(
				camPos.getX() + screenShakeX,
				camPos.getY() + screenShakeY,
				camPos.getZ() + screenShakeZ
		             );

		screenShake *= 0.9f;

		// shooting
		float distanceMultiplier = 3.0f;
		Vector3f dir = camera.getDirectionAt().normalized();
		float soundPosX = camera.getX() + dir.getX() * distanceMultiplier;
		float soundPosY = camera.getY() + dir.getY() * distanceMultiplier;
		float soundPosZ = camera.getZ() + dir.getZ() * distanceMultiplier;

		for (MouseButtonEvent event : inputHandler.getMouseButtonEvents()) {
			if (event.action == GLFW.GLFW_PRESS && event.button == 0) {
				map.makeRequest(new ShootRequestPacket(ShootRequestPacket.TYPE_SHOTGUN, camPos, camera.getDirectionAt()));
				soundSystem.playSound("res/sound/player/shoot-shotgun.ogg", soundPosX, soundPosY, soundPosZ);
			}
		}
	}

}
