package game12.client.systems;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.render.Camera;
import de.nerogar.noise.sound.Sound;
import de.nerogar.noise.sound.SoundListener;
import de.nerogar.noise.sound.SoundOGGLoader;
import game12.ClientMain;
import game12.client.components.SoundComponent;
import game12.client.map.ClientMap;
import game12.core.LogicSystem;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.util.ArrayList;
import java.util.List;

public class SoundSystem extends LogicSystem {

	private ClientMap map;

	private SoundListener soundListener;
	private List<Sound>   sounds;

	public SoundSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		sounds = new ArrayList<>();

		soundListener = new SoundListener();

		getEventManager().register(UpdateEvent.class, this::onUpdate);

		AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
	}

	private void onUpdate(UpdateEvent event) {
		InputHandler inputHandler = ClientMain.window.getInputHandler();

		Camera camera = getContainer().getSystem(RenderSystem.class).getCamera();

		soundListener.setPosition(camera.getX(), 0, camera.getZ());
		soundListener.setDirection(camera.getDirectionAt(), camera.getDirectionUp());

		sounds.forEach(Sound::update);
		sounds.removeIf(Sound::isDone);

		for (SoundComponent soundComponent : map.getEntityList().getComponents(SoundComponent.class)) {
			PositionComponent positionComponent = soundComponent.getEntity().getComponent(PositionComponent.class);
			soundComponent.sound.setPosition(
					positionComponent.getX(),
					positionComponent.getY(),
					positionComponent.getZ()
			                                );
		}
	}

	public Sound playSound(String filename, float x, float y, float z) {
		return playSound(filename, x, y, z, 10);
	}

	public Sound playSound(String filename, float x, float y, float z, float maxDistacne) {
		Sound sound = SoundOGGLoader.loadSound(true, filename);
		AL10.alSourcef(sound.getAlSourceHandle(), AL10.AL_ROLLOFF_FACTOR, 1);
		AL10.alSourcef(sound.getAlSourceHandle(), AL10.AL_REFERENCE_DISTANCE, 1);
		AL10.alSourcef(sound.getAlSourceHandle(), AL10.AL_MAX_DISTANCE, maxDistacne);

		sound.setPosition(x, y, z);
		sound.play();
		sounds.add(sound);

		return sound;
	}

}
