package net.gegy1000.slyther.server;

import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.entity.Entity;
import net.gegy1000.slyther.game.entity.Sector;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.network.message.server.MessageSetup;
import net.gegy1000.slyther.server.game.entity.ServerSnake;
import net.gegy1000.slyther.util.Log;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

public class ConnectedClient {
    public String name;
    public Skin skin;
    public ServerSnake snake;
    public WebSocket socket;
    public long lastPacketTime = System.currentTimeMillis();
    public SlytherServer server;
    public float scale = 0.9F;
    public int protocolVersion;
    public int rank;
    public float viewDistance;
    public int id;

    public List<Entity> trackingEntities = new ArrayList<>();
    public List<Sector> trackingSectors = new ArrayList<>();

    public ConnectedClient(SlytherServer server, WebSocket socket, int id) {
        this.server = server;
        this.socket = socket;
        this.id = id;
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
            server.scheduleTask(() -> {
                snake = server.createSnake(this);
                track(snake);
                return null;
            });
        }
    }

    public void track(Entity entity) {
        if (!trackingEntities.contains(entity)) {
            trackingEntities.add(entity);
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
        if (trackingEntities.remove(entity)) {
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
            if (scale != newScale) {
                if (scale < newScale) {
                    scale += 0.0001F;
                    if (scale > newScale) {
                        scale = newScale;
                    }
                } else if (scale > newScale) {
                    scale -= 0.0001F;
                    if (scale < newScale) {
                        scale = newScale;
                    }
                }
            }
            viewDistance = 700.0F / scale;
            for (Sector sector : server.getSectors()) {
                if (sector.shouldTrack(this)) {
                    trackSector(sector);
                } else {
                    untrackSector(sector);
                }
            }
            List<Entity> entities = new ArrayList<>();
            for (Sector sector : trackingSectors) {
                entities.addAll(server.getMovingEntitiesInSector(sector));
            }
            List<Entity> untrack = new ArrayList<>();
            List<Entity> track = new ArrayList<>();
            for (Entity tracking : trackingEntities) {
                if (tracking.canMove()) {
                    if (!entities.contains(tracking)) {
                        untrack.add(tracking);
                    } else {
                        track.add(tracking);
                    }
                }
            }
            for (Entity entity : untrack) {
                untrack(entity);
            }
            for (Entity entity : track) {
                track(entity);
            }
        }
    }

    public void send(SlytherServerMessageBase message) {
        if (socket.isOpen()) {
            try {
                MessageByteBuffer buffer = new MessageByteBuffer();
                long time = System.currentTimeMillis();
                buffer.writeUInt16((int) (time - lastPacketTime));
                buffer.writeUInt8(message.getSendMessageId());
                message.write(buffer, server, this);
                lastPacketTime = time;
                socket.send(buffer.bytes());
            } catch (Exception e) {
                Log.error("An error occurred while sending message {} to {} ({})", message.getClass().getName(), name, id);
                Log.catching(e);
            }
        } else if (server.clients.contains(this)) {
            server.removeClient(socket);
        }
    }
}
