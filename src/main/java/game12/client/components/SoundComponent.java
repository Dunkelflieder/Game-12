package game12.client.components;

import de.nerogar.noise.sound.Sound;
import game12.client.systems.SoundSystem;
import game12.core.components.PositionComponent;
import game12.core.map.Component;

public class SoundComponent extends Component {

	public Sound sound;

	@Override
	protected void initSystems() {
		sound = getEntity().getMap().getSystem(SoundSystem.class).playSound(
				"res/sound/turretProjectile/flame.ogg",
				getEntity().getComponent(PositionComponent.class).getX(),
				getEntity().getComponent(PositionComponent.class).getY() + 1.2f,
				getEntity().getComponent(PositionComponent.class).getZ()
		                                                                         );
		sound.setLoop(true);
	}

	@Override
	public Component clone() {
		return new SoundComponent();
	}
}
