package net.gegy1000.slyther.network;

import net.gegy1000.slyther.network.message.*;

import java.util.HashMap;
import java.util.Map;

public enum MessageHandler {
    INSTANCE;

    private final Map<Byte, Class<? extends SlytherServerMessageBase>> SERVER_MESSAGES = new HashMap<>();

    MessageHandler() {
        this.registerServer(MessagePing.class);
        this.registerServer(MessageSetup.class);
        this.registerServer(MessageNewSnake.class);
        this.registerServer(MessageSendSector.class);
        this.registerServer(MessageNewFood.class);
        this.registerServer(MessageNewPrey.class);
        this.registerServer(MessageUpdateSnake.class);
        this.registerServer(MessageUpdateSnakeParts.class);
        this.registerServer(MessagePreyPositionUpdate.class);
        this.registerServer(MessageUpdateFam.class);
        this.registerServer(MessageRemoveFood.class);
        this.registerServer(MessageRemoveSnakePart.class);
        this.registerServer(MessageUpdateLeaderboard.class);
        this.registerServer(MessageUpdateLongestPlayer.class);
        this.registerServer(MessageAddSector.class);
        this.registerServer(MessageRemoveSector.class);
        this.registerServer(MessagePlayerDeath.class);
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
