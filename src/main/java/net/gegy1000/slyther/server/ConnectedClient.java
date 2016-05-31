package net.gegy1000.slyther.server;

import java.util.ArrayList;
import java.util.List;

import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.entity.Entity;
import net.gegy1000.slyther.game.entity.Sector;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.network.message.server.MessageSetup;
import net.gegy1000.slyther.server.game.entity.ServerSnake;
import net.gegy1000.slyther.util.Log;

import org.java_websocket.WebSocket;

public class ConnectedClient {
    public String name;
    public Skin skin;
    public ServerSnake snake;
    public WebSocket socket;
    public long lastPacketTime;
    public SlytherServer server;
    public float gsc = 0.9F;
    public int protocolVersion;
    public int rank;
    public float viewDistance;

    public List<Entity> tracking = new ArrayList<>();
    public List<Sector> trackingSectors = new ArrayList<>();

    public ConnectedClient(SlytherServer server, WebSocket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void setup(String name, Skin skin, int protocolVersion) {
        this.name = name;
        this.skin = skin;
        if (protocolVersion > 31) {
            protocolVersion = 1;
        }
        this.protocolVersion = protocolVersion;
        if (protocolVersion >= 8) {
            send(new MessageSetup());
            snake = server.createSnake(this);
            track(snake);
        }
    }

    public void track(Entity entity) {
        if (!tracking.contains(entity)) {
            tracking.add(entity);
            entity.startTracking(this);
        }
    }

    public void trackSector(Sector sector) {
        if (!trackingSectors.contains(sector)) {
            trackingSectors.add(sector);
            sector.startTracking(this);
        }
    }

    public void untrack(Entity entity) {
        if (tracking.remove(entity)) {
            entity.stopTracking(this);
        }
    }

    public void untrackSector(Sector sector) {
        if (trackingSectors.remove(sector)) {
            sector.stopTracking(this);
        }
    }

    public void update() {
        if (snake != null) {
            float newScale = 0.4F / Math.max(1.0F, (snake.sct + 16.0F) / 36.0F) + 0.5F;
            if (gsc != newScale) {
                if (gsc < newScale) {
                    gsc += 0.0001F;
                    if (gsc > newScale) {
                        gsc = newScale;
                    }
                } else if (gsc > newScale) {
                    gsc -= 0.0001F;
                    if (gsc < newScale) {
                        gsc = newScale;
                    }
                }
            }
            viewDistance = 800.0F / gsc;
            for (Sector sector : server.getSectors()) {
                if (sector.shouldTrack(this)) {
                    trackSector(sector);
                } else {
                    untrackSector(sector);
                }
            }
            for (Entity entity : server.getEntities()) {
                entity.updateTrackers(this);
            }
        }
    }

    public void send(SlytherServerMessageBase message) {
        try {
            MessageByteBuffer buffer = new MessageByteBuffer();
            buffer.writeUInt16((int) (System.currentTimeMillis() - lastPacketTime));
            buffer.writeUInt8(message.getSendMessageId());
            message.write(buffer, server, this);
            socket.send(buffer.bytes());
        } catch (Exception e) {
            Log.error("An error occurred while sending message {}", message.getClass().getName());
            Log.catching(e);
        }
    }
}
