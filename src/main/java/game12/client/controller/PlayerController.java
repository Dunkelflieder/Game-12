package game12.client.controller;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.GLWindow;
import game12.client.Controller;
import game12.client.gui.GuiContainer;
import game12.client.map.ClientMap;

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
