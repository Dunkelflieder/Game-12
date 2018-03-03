package strategy.client;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.Connection;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.RenderHelper;
import strategy.ClientMain;
import strategy.Strategy;
import strategy.client.gui.Gui;
import strategy.client.gui.GuiContainer;
import strategy.client.gui.MainGui;
import strategy.core.network.packets.ClientPacket;
import strategy.server.ServerMainThread;

import java.io.IOException;
import java.net.Socket;

public class Menu {

	private GLWindow     window;
	private GuiContainer guiContainer;
	private EventManager eventManager;

	// current game
	private boolean          activeGame;
	private ServerMainThread serverMainThread;
	private Connection       connection;
	private INetworkAdapter networkAdapter;

	public Menu(GLWindow window, GuiContainer guiContainer, EventManager eventManager) {
		this.window = window;
		this.guiContainer = guiContainer;
		this.eventManager = eventManager;

		// setup main gui
		Gui mainGui = new MainGui(this, eventManager);
		guiContainer.setActiveGui(mainGui);
	}

	public boolean startLocalGame() {
		if (activeGame) return false;
		activeGame = true;

		try {
			// start server
			serverMainThread = new ServerMainThread();

			// connection
			connection = new Connection(new Socket("localhost", 34543));
			networkAdapter = connection.getNetworkAdapter(Strategy.NETWORK_ADAPTER_DEFAULT);

			// start client
			Client client = new Client(connection, window, eventManager, guiContainer);
			ClientMain.setClient(client);

			// start game
			ClientPacket startPacket = new ClientPacket();
			startPacket.setCommand(ClientPacket.COMMAND_START);
			startPacket.setMapID("save1");
			networkAdapter.send(startPacket);

		} catch (IOException e) {
			e.printStackTrace(Strategy.logger.getErrorStream());
		}

		return true;
	}

	public boolean startServer() {
		if (activeGame) return false;
		activeGame = true;

		try {
			// start server
			serverMainThread = new ServerMainThread();

			// connection
			connection = new Connection(new Socket("localhost", 34543));

			// start client
			Client client = new Client(connection, window, eventManager, guiContainer);
			ClientMain.setClient(client);

		} catch (IOException e) {
			e.printStackTrace(Strategy.logger.getErrorStream());
		}

		return true;
	}

	public boolean connectLocal() {
		if (activeGame) return false;
		activeGame = true;

		try {
			// connection
			connection = new Connection(new Socket("localhost", 34543));

			// start client
			Client client = new Client(connection, window, eventManager, guiContainer);
			ClientMain.setClient(client);
		} catch (IOException e) {
			e.printStackTrace(Strategy.logger.getErrorStream());
		}

		return true;
	}

	public boolean connectStartServer() {
		if (activeGame) return false;
		activeGame = true;

		try {
			// connection
			connection = new Connection(new Socket("localhost", 34543));

			// start client
			Client client = new Client(connection, window, eventManager, guiContainer);
			ClientMain.setClient(client);

			// start game
			ClientPacket startPacket = new ClientPacket();
			startPacket.setCommand(ClientPacket.COMMAND_START);
			startPacket.setMapID("save1");
			networkAdapter.send(startPacket);

		} catch (IOException e) {
			e.printStackTrace(Strategy.logger.getErrorStream());
		}

		return true;
	}

	public void update(float timeDelta) {
		guiContainer.update(window.getInputHandler(), timeDelta);

		if (connection != null) connection.flushPackets();
	}

	public void render() {
		guiContainer.render();

		window.bind();
		RenderHelper.blitTexture(guiContainer.getColorOutput());
	}

}
