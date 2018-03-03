package strategy.client;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.Connection;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.RenderHelper;
import de.nerogar.noise.util.Color;
import strategy.Strategy;
import strategy.client.controller.PlayerController;
import strategy.client.event.RenderEvent;
import strategy.client.event.SystemSyncEvent;
import strategy.client.event.WindowSizeChangeEvent;
import strategy.client.gui.GuiContainer;
import strategy.client.map.ClientMap;
import strategy.client.map.ClientMapLoader;
import strategy.client.systems.RenderSystem;
import strategy.core.Faction;
import strategy.core.SystemSyncParameter;
import strategy.core.event.StartGameEvent;
import strategy.core.event.UpdateEvent;
import strategy.core.network.StrategyPacketInfo;
import strategy.core.network.packets.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Client {

	private Connection      connection;
	private INetworkAdapter networkAdapter;
	private INetworkAdapter wildcardNetworkAdapter;
	private GLWindow        window;
	private EventManager    eventManager;

	private boolean isRunning;

	// current active game session
	private ClientGameSystemContainer         gameSystemContainer;
	private ClientMapSystemContainer[]        mapSystemContainers;
	private ClientFactionSystemContainer      factionSystemContainer;
	private ClientFactionMapSystemContainer[] factionMapSystemContainers;
	private List<ClientMap>                   currentMaps;
	private Faction[]                         factions;
	private Faction                           ownFaction;
	private Controller                        controller;

	// gui
	private GuiContainer guiContainer;

	private EventListener<WindowSizeChangeEvent> windowSizeChangeListener;

	private ClientMapLoader clientMapLoader;
	private boolean         clientMapLoaderDone;

	public Client(Connection connection, GLWindow window, EventManager eventManager, GuiContainer guiContainer) {
		this.connection = connection;
		this.networkAdapter = connection.getNetworkAdapter(Strategy.NETWORK_ADAPTER_DEFAULT);
		this.wildcardNetworkAdapter = connection.getWildcardNetworkAdapter();
		this.window = window;
		this.eventManager = eventManager;

		this.guiContainer = guiContainer;

		windowSizeChangeListener = this::onWindowResize;
		eventManager.register(WindowSizeChangeEvent.class, windowSizeChangeListener);
	}

	private void onWindowResize(WindowSizeChangeEvent event) {
	}

	public void update(float timeDelta) {
		connection.pollPackets(false);

		// gui
		guiContainer.update(window.getInputHandler(), timeDelta);

		// client
		processInit();
		updateServerPackets();
		updateSystemsPackets();
		if (currentMaps != null && isRunning) {
			updateClient(timeDelta);
		}

	}

	private void processInit() {
		if (clientMapLoader != null && clientMapLoader.isDone()) {
			clientMapLoader.finalizeLoad();
			initSystemsWithData();
			clientMapLoader = null;
			clientMapLoaderDone = true;
		}

		if (currentMaps == null) return;
		if (clientMapLoaderDone) {
			networkAdapter.send(new ClientMapLoadProgress(0, true));
		}
	}

	public void render(float timeDelta) {

		// render map
		if (currentMaps != null) {
			window.bind();
			eventManager.trigger(new RenderEvent());
		}

		// render gui
		guiContainer.render();

		// blit
		window.bind();

		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (isRunning) displayMap();
		RenderHelper.overlayPremultipliedTexture(guiContainer.getColorOutput());
	}

	private void displayMap() {
		for (int i = 0; i < mapSystemContainers.length; i++) {
			RenderSystem renderSystem = mapSystemContainers[i].getSystem(RenderSystem.class);
			if (renderSystem != null) {
				if (renderSystem.getMap().isActive()) {
					RenderHelper.overlayTexture(renderSystem.getRenderer().getColorOutput());
				}
			}

		}
	}

	private void updateServerPackets() {
		List<Packet> packets = wildcardNetworkAdapter.getPackets(StrategyPacketInfo.CONTROL_PACKET_CHANNEL);

		for (Packet p : packets) {
			if (p instanceof InitSystemPacket) {
				InitSystemPacket packet = (InitSystemPacket) p;
				initSystem(packet);

			} else if (p instanceof InitSystemContainerPacket) {
				InitSystemContainerPacket packet = (InitSystemContainerPacket) p;
				initSystemContainer(packet);

			} else if (p instanceof FactionInfoPacket) {
				FactionInfoPacket packet = (FactionInfoPacket) p;

				factions = new Faction[packet.getFactionIDs().length];
				for (int i = 0; i < factions.length; i++) {
					factions[i] = new Faction(packet.getFactionIDs()[i], new Color(packet.getFactionColors()[i]));
				}

				ownFaction = factions[packet.getOwnFaction()];

			} else if (p instanceof MapInfoPacket) {
				MapInfoPacket packet = (MapInfoPacket) p;
				createMap(packet.getMapID());

			} else if (p instanceof ServerPacket) {
				ServerPacket packet = (ServerPacket) p;

				switch (packet.getCommand()) {
					case ServerPacket.COMMAND_CREATE_SYSTEMS:
						createSystems();
						break;
					case ServerPacket.COMMAND_LOAD_MAP:
						loadMap();
						break;
					case ServerPacket.COMMAND_INIT_CONTROLLER:
						setupController();
						break;
					case ServerPacket.COMMAND_START:
						isRunning = true;
						eventManager.trigger(new StartGameEvent());
						break;
				}
			}
		}

	}

	private void initSystemContainer(InitSystemContainerPacket packet) {
		gameSystemContainer.initContainer(packet);
		for (ClientMapSystemContainer mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initContainer(packet);
		}
		for (ClientFactionMapSystemContainer factionMapSystemContainer : factionMapSystemContainers) {
			factionMapSystemContainer.initContainer(packet);
		}
		factionSystemContainer.initContainer(packet);
	}

	private void initSystem(InitSystemPacket packet) {
		gameSystemContainer.initSystem(packet);
		for (ClientMapSystemContainer mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initSystem(packet);
		}
		for (ClientFactionMapSystemContainer factionMapSystemContainer : factionMapSystemContainers) {
			factionMapSystemContainer.initSystem(packet);
		}
		factionSystemContainer.initSystem(packet);
	}

	private void initSystemsWithData() {
		gameSystemContainer.initSystemsWithData();
		for (ClientMapSystemContainer mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initSystemsWithData();
		}
		factionSystemContainer.initSystemsWithData();
		for (int i = 0; i < currentMaps.size(); i++) {
			factionMapSystemContainers[i].initSystemsWithData();
		}
	}

	private void updateSystemsPackets() {
		List<Packet> packets = networkAdapter.getPackets(StrategyPacketInfo.SYSTEMS_PACKET_CHANNEL);

		for (Packet p : packets) {
			if (p instanceof SystemSyncParameter) {
				eventManager.trigger(new SystemSyncEvent((SystemSyncParameter) p));
			}
		}

	}

	private void createSystems() {
		// game system
		gameSystemContainer = new ClientGameSystemContainer(eventManager, networkAdapter, mapSystemContainers);

		// map systems
		mapSystemContainers = new ClientMapSystemContainer[currentMaps.size()];
		for (int i = 0; i < mapSystemContainers.length; i++) {
			mapSystemContainers[i] = new ClientMapSystemContainer(currentMaps.get(i));
			currentMaps.get(i).setSystemContainer(mapSystemContainers[i], gameSystemContainer);
		}

		// faction system
		factionSystemContainer = new ClientFactionSystemContainer(eventManager, ownFaction);
		ownFaction.setSystemContainer(factionSystemContainer, gameSystemContainer);

		// faction-map systems
		factionMapSystemContainers = new ClientFactionMapSystemContainer[currentMaps.size()];
		for (int i = 0; i < currentMaps.size(); i++) {
			factionMapSystemContainers[i] = new ClientFactionMapSystemContainer(currentMaps.get(i), ownFaction, i);
		}

	}

	private void createMap(String mapID) {
		currentMaps = new ArrayList<>();
		clientMapLoader = new ClientMapLoader(currentMaps, mapID, connection, factions);
		clientMapLoader.loadMeta();

		for (ClientMap currentMap : currentMaps) {
			eventManager.addTriggerChild(currentMap.getEventManager());
		}

	}

	private void loadMap() {
		clientMapLoader.startLoading();
	}

	private void setupController() {
		controller = new PlayerController(window, eventManager, currentMaps, networkAdapter, guiContainer);

		//controller = new PlayerController(window, eventManager, currentMaps, ownFaction, networkAdapter, guiContainer);
		//controller = new EditorController(window, currentMaps, connection, camera, guiContainer);
	}

	private void closeMap() {
		for (int i = 0; i < currentMaps.size(); i++) {
			currentMaps.get(i).cleanup();
			eventManager.removeTriggerChild(currentMaps.get(i).getEventManager());
		}

		currentMaps = null;
	}

	private void updateClient(float timeDelta) {
		// input
		controller.update(timeDelta);

		eventManager.trigger(new UpdateEvent(timeDelta));
	}

	public Connection getConnection() {
		return connection;
	}

	private void cleanup() {
		gameSystemContainer.cleanup();
		eventManager.unregister(WindowSizeChangeEvent.class, windowSizeChangeListener);
		closeMap();
	}

}
