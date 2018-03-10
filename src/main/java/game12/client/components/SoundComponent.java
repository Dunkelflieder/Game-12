package game12.client.components;

import de.nerogar.noise.serialization.NDSException;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.sound.Sound;
import game12.client.systems.SoundSystem;
import game12.core.components.PositionComponent;
import game12.core.map.Component;
import game12.core.systems.GameObjectsSystem;

public class SoundComponent extends Component {

	private String soundFile;
	public Sound sound;

	public SoundComponent() {
	}

	public SoundComponent(String soundFile) {
		this.soundFile = soundFile;
	}

	@Override
	public void setData(GameObjectsSystem gameObjectsSystem, NDSNodeObject data) throws NDSException {
		this.soundFile = data.getStringUTF8("soundFile");
	}

	@Override
	protected void initSystems() {
		sound = getEntity().getMap().getSystem(SoundSystem.class).playSound(
				soundFile,
				getEntity().getComponent(PositionComponent.class).getX(),
				getEntity().getComponent(PositionComponent.class).getY() + 1.2f,
				getEntity().getComponent(PositionComponent.class).getZ()
		                                                                   );
		sound.setLoop(true);
	}

	@Override
	protected void cleanup() {
		sound.cleanup();
	}

	@Override
	public Component clone() {
		return new SoundComponent(soundFile);
	}
}
