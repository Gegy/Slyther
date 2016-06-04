package net.gegy1000.slyther.network;

import net.gegy1000.slyther.network.message.*;
import net.gegy1000.slyther.network.message.client.*;
import net.gegy1000.slyther.network.message.server.*;
import net.gegy1000.slyther.util.Log;

import java.util.HashMap;
import java.util.Map;

public enum MessageHandler {
    INSTANCE;

    private final Map<Byte, Class<? extends SlytherServerMessageBase>> SERVER_MESSAGES = new HashMap<>();

    MessageHandler() {
        registerServer(MessagePing.class);
        registerServer(MessageSetup.class);
        registerServer(MessageNewSnake.class);
        registerServer(MessagePopulateSector.class);
        registerServer(MessageNewFood.class);
        registerServer(MessageNewPrey.class);
        registerServer(MessageUpdateSnake.class);
        registerServer(MessageSnakeMovement.class);
        registerServer(MessagePreyPositionUpdate.class);
        registerServer(MessageUpdateFam.class);
        registerServer(MessageRemoveFood.class);
        registerServer(MessageRemoveSnakePoint.class);
        registerServer(MessageUpdateLeaderboard.class);
        registerServer(MessageUpdateLongestPlayer.class);
        registerServer(MessageAddSector.class);
        registerServer(MessageRemoveSector.class);
        registerServer(MessagePlayerDeath.class);
        registerServer(MessageUpdateMap.class);
    }

    public void registerServer(Class<? extends SlytherServerMessageBase> message) {
        try {
            SlytherServerMessageBase messageObject = message.getConstructor().newInstance();
            for (int id : messageObject.getMessageIds()) {
                SERVER_MESSAGES.put((byte) id, message);
            }
        } catch (Exception e) {
            Log.error("Error while registering message {}", message.getName());
            Log.catching(e);
        }
    }

    public SlytherClientMessageBase getClientMessage(MessageByteBuffer buffer) {
        if (buffer.limit() == 1) {
            int type = buffer.readUInt8();
            if (type == 251) {
                return new MessageClientPing();
            } else if (type == 253 || type == 254) {
                return new MessageAccelerate(type == 253);
            } else {
                buffer.skipBytes(-1);
                return new MessageSetAngle();
            }
        } else if (buffer.limit() > 1) {
            int type = buffer.readUInt8();
            if (type == 's') {
                return new MessageClientSetup();
            } else if (type == 252) {
                return new MessageSetTurn();
            }
        }
        return null;
    }

    public Class<? extends SlytherServerMessageBase> getServerMessage(byte id) {
        return SERVER_MESSAGES.get(id);
    }
}
