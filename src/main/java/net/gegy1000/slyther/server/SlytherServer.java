package net.gegy1000.slyther.server;

import net.gegy1000.slyther.game.ConfigHandler;
import net.gegy1000.slyther.util.SystemUtils;
import org.java_websocket.WebSocket;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SlytherServer {
    public ServerConfig configuration;

    public ServerNetworkManager networkManager;

    private static final File CONFIGURATION_FILE = new File(SystemUtils.getGameFolder(), "server/config.json");
    public List<ConnectedClient> clients = new ArrayList<>();

    public SlytherServer() {
        try {
            configuration = ConfigHandler.INSTANCE.readConfig(CONFIGURATION_FILE, ServerConfig.class);
            ConfigHandler.INSTANCE.saveConfig(CONFIGURATION_FILE, configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            networkManager = new ServerNetworkManager(this, configuration.serverPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(WebSocket socket) {
        ConnectedClient connectedClient = getConnectedClient(socket);
        clients.remove(connectedClient);
        if (connectedClient != null) {
            System.out.println(connectedClient.name + " disconnected.");
        }
    }

    public ConnectedClient getConnectedClient(WebSocket socket) {
        for (ConnectedClient client : clients) {
            if (client.socket.equals(socket)) {
                return client;
            }
        }
        return null;
    }
}
