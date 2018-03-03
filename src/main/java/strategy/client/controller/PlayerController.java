package strategy.client.controller;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.GLWindow;
import strategy.client.Controller;
import strategy.client.gui.GuiContainer;
import strategy.client.map.ClientMap;

import java.util.List;

public class PlayerController extends Controller {

	public PlayerController(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter,
			GuiContainer guiContainer) {
		super(window, eventManager, maps, networkAdapter, guiContainer);



	}

	@Override
	public void update(float timeDelta) {

	}


}
