package game12.server;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.Connection;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.network.ServerThread;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Logger;
import de.nerogar.noise.util.Timer;
import game12.Game12;
import game12.core.*;
import game12.core.event.StartGameEvent;
import game12.core.event.UpdateEvent;
import game12.core.network.StrategyPacketInfo;
import game12.core.network.packets.*;
import game12.server.map.ServerMap;
import game12.server.map.ServerMapLoader;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerMainThread extends Thread {

	public static final int   TICK_RATE    = 60;
	public static       float SPEED_FACTOR = 1.0f;

	private boolean serverRunning;

	private boolean isRunning;

	private EventManager eventManager;

	private ServerThread     serverThread;
	private INetworkAdapter  networkAdapter;
	private List<Connection> connections;

	// current active game session
	private GameSystemContainer           gameSystemContainer;
	private ServerMapSystemContainer[]    mapSystemContainers;
	private FactionSystemContainer[]      factionSystemContainers;
	private FactionMapSystemContainer[][] factionMapSystemContainers;
	private List<ServerMap>               currentMaps;
	private Faction[]                     factions;

	private Map<Connection, ClientMapLoadProgress> clientMapLoadProgressMap;
	private boolean                                serverMapLoadDone;
	private ServerMapLoader                        serverMapLoader;

	public ServerMainThread() {
		try {
			serverThread = new ServerThread(34543);
			networkAdapter = serverThread.getNetworkAdapter(Game12.NETWORK_ADAPTER_DEFAULT);
		} catch (BindException e) {
			e.printStackTrace();
		}

		eventManager = new EventManager("ServerMain");

		connections = new ArrayList<>();
		clientMapLoadProgressMap = new HashMap<>();

		setName("ServerMainThread");
		setDaemon(true);
		serverRunning = true;
		start();
	}

	private void loop(float timeDelta) {
		processMapLoader();
		processPackets();

		if (currentMaps != null && isRunning) {
			eventManager.trigger(new UpdateEvent(timeDelta * SPEED_FACTOR));
		}

		for (Connection connection : connections) {
			connection.flushPackets();
		}
	}

	private void processMapLoader() {
		if (serverMapLoader != null) {
			if (serverMapLoader.isDone()) {
				serverMapLoader.finalizeLoad();
				initSystemsWithData();
				serverMapLoadDone = true;
				serverMapLoader = null;
			}
		}

		if (serverMapLoadDone) {

			boolean done = true;
			for (Connection c : connections) {
				ClientMapLoadProgress clientMapLoadProgress = clientMapLoadProgressMap.get(c);
				if (clientMapLoadProgress == null || !clientMapLoadProgress.isDone()) done = false;
			}

			if (done) {
				sendStart();
				serverMapLoadDone = false;
			}

		}

	}

	private void processPackets() {
		connections.removeIf(Connection::isClosed);

		if (currentMaps == null) {
			connections.addAll(serverThread.getNewConnections());
		} else {
			// game is running, close all new connections
			for (Connection connection : serverThread.getNewConnections()) {
				connection.close();
			}
		}

		for (Connection connection : connections) {
			// TODO maybe some packets can get lost here on the first poll
			connection.pollPackets(false);

			for (Packet p : networkAdapter.getPackets(StrategyPacketInfo.CONTROL_PACKET_CHANNEL)) {

				if (p instanceof ClientPacket) {
					ClientPacket packet = (ClientPacket) p;

					int command = packet.getCommand();

					if (command == ClientPacket.COMMAND_START) {
						startCommand(packet.getMapID());
					} else if (command == ClientPacket.COMMAND_STOP) {
						stopCommand();
					} else if (command == ClientPacket.COMMAND_PAUSE) {
						pauseCommand();
					} else if (command == ClientPacket.COMMAND_RESUME) {
						resumeCommand();
					}
				} else if (p instanceof ClientMapLoadProgress) {
					ClientMapLoadProgress packet = (ClientMapLoadProgress) p;

					clientMapLoadProgressMap.put(connection, packet);
				}
			}
		}

	}

	private void initSystemsWithData() {
		gameSystemContainer.initSystemsWithData();
		for (ServerMapSystemContainer mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initSystemsWithData();
		}
		for (FactionSystemContainer factionSystemContainer : factionSystemContainers) {
			factionSystemContainer.initSystemsWithData();
		}
		for (int i = 0; i < factions.length; i++) {
			for (int j = 0; j < currentMaps.size(); j++) {
				factionMapSystemContainers[i][j].initSystemsWithData();
			}
		}
	}

	private void startCommand(String mapID) {
		if (currentMaps != null) {
			Game12.logger.log(Logger.WARNING, "start packet received while game is running");
		}

		// TODO for now, only one factions exists, everyone is in that faction
		factions = new Faction[] { new Faction((byte) 1, new Color(0f, 0f, 1f, 1f)) };
		for (Connection connection : connections) {
			factions[0].addNetworkAdapter(connection.getNetworkAdapter(Game12.NETWORK_ADAPTER_DEFAULT));
		}

		currentMaps = new ArrayList<>();

		// load map meta
		serverMapLoader = new ServerMapLoader(currentMaps, mapID, serverThread, factions);
		serverMapLoader.loadMeta();
		for (ServerMap currentMap : currentMaps) {
			eventManager.addTriggerChild(currentMap.getEventManager());
		}

		// create system containers
		// game system
		gameSystemContainer = new ServerGameSystemContainer(eventManager, networkAdapter, mapSystemContainers);

		// map systems
		mapSystemContainers = new ServerMapSystemContainer[currentMaps.size()];
		for (int i = 0; i < mapSystemContainers.length; i++) {
			mapSystemContainers[i] = new ServerMapSystemContainer(currentMaps.get(i));
			currentMaps.get(i).setSystemContainer(mapSystemContainers[i], gameSystemContainer);
		}


		// faction systems
		factionSystemContainers = new FactionSystemContainer[factions.length];
		for (int i = 0; i < factions.length; i++) {
			ServerFactionSystemContainer factionSystemContainer = new ServerFactionSystemContainer(eventManager, networkAdapter, factions[i]);
			factions[i].setSystemContainer(factionSystemContainer, gameSystemContainer);

			factionSystemContainers[i] = factionSystemContainer;
		}

		// faction-map systems
		factionMapSystemContainers = new FactionMapSystemContainer[factions.length][currentMaps.size()];
		for (int i = 0; i < factions.length; i++) {
			for (int j = 0; j < currentMaps.size(); j++) {
				ServerFactionMapSystemContainer factionMapSystemContainer = new ServerFactionMapSystemContainer(currentMaps.get(j), factions[i], j);

				factionMapSystemContainers[i][j] = factionMapSystemContainer;
			}
		}

		// send faction info
		networkAdapter.send(new FactionInfoPacket(factions, (byte) 0));

		// tell clients which map to load
		networkAdapter.send(new MapInfoPacket(mapID));

		// tell clients to create systems
		networkAdapter.send(new ServerPacket(ServerPacket.COMMAND_CREATE_SYSTEMS));

		// initialize all systems and synchronize them with clients
		gameSystemContainer.initSystems();
		for (ServerMapSystemContainer mapSystemContainer : mapSystemContainers) {
			mapSystemContainer.initSystems();
		}
		for (FactionSystemContainer factionSystemContainer : factionSystemContainers) {
			factionSystemContainer.initSystems();
		}
		for (int i = 0; i < factions.length; i++) {
			for (int j = 0; j < currentMaps.size(); j++) {
				factionMapSystemContainers[i][j].initSystems();
			}
		}

		// tell clients to load the map
		networkAdapter.send(new ServerPacket(ServerPacket.COMMAND_LOAD_MAP));

		// load the map
		serverMapLoader.startLoading();
	}

	private void sendStart() {
		// tell clients to initialize the controller
		networkAdapter.send(new ServerPacket(ServerPacket.COMMAND_INIT_CONTROLLER));

		// start the game
		networkAdapter.send(new ServerPacket(ServerPacket.COMMAND_START));
		eventManager.trigger(new StartGameEvent());

		isRunning = true;
	}

	private void stopCommand() {
		Game12.logger.log(Logger.WARNING, "stop command received, not implemented yet");
	}

	private void pauseCommand() {
		Game12.logger.log(Logger.WARNING, "pause command received, not implemented yet");
	}

	private void resumeCommand() {
		Game12.logger.log(Logger.WARNING, "resume command received, not implemented yet");
	}

	@Override
	public void run() {
		Timer timer = new Timer();

		while (serverRunning) {
			timer.update(1f / TICK_RATE);

			loop(timer.getDelta());
		}
	}

}
