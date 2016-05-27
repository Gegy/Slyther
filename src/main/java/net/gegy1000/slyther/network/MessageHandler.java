package net.gegy1000.slyther.network;

import net.gegy1000.slyther.network.message.*;

import java.util.HashMap;
import java.util.Map;

public enum MessageHandler {
    INSTANCE;

    private final Map<Byte, Class<? extends SlytherServerMessageBase>> SERVER_MESSAGES = new HashMap<>();

    MessageHandler() {
        registerServer(MessagePing.class);
        registerServer(MessageSetup.class);
        registerServer(MessageNewSnake.class);
        registerServer(MessageUpdateSectorFoods.class);
        registerServer(MessageNewFood.class);
        registerServer(MessageNewPrey.class);
        registerServer(MessageUpdateSnake.class);
        registerServer(MessageUpdateSnakePoints.class);
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
            System.err.println("Error while registering message " + message.getName());
            e.printStackTrace();
        }
    }

    public Class<? extends SlytherServerMessageBase> getServerMessage(byte id) {
        return SERVER_MESSAGES.get(id);
    }
}
