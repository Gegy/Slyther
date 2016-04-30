package net.gegy1000.slyther.network;

import net.gegy1000.slyther.network.message.*;

import java.util.HashMap;
import java.util.Map;

public enum MessageHandler {
    INSTANCE;

    private final Map<Byte, Class<? extends SlytherServerMessageBase>> SERVER_MESSAGES = new HashMap<>();

    MessageHandler() {
        this.registerServer(MessageSetup.class);
        this.registerServer(MessageNewSnake.class);
        this.registerServer(MessagePositionUpdate.class);
        this.registerServer(MessageAddFood.class);
        this.registerServer(MessageFoodEaten.class);
    }

    public void registerServer(Class<? extends SlytherServerMessageBase> message) {
        try {
            SlytherServerMessageBase messageObject = message.getConstructor().newInstance();
            SERVER_MESSAGES.put((byte) messageObject.getMessageId(), message);
        } catch (Exception e) {
            System.err.println("Error while registering message " + message.getName());
            e.printStackTrace();
        }
    }

    public Class<? extends SlytherServerMessageBase> getServerMessage(byte id) {
        return SERVER_MESSAGES.get(id);
    }
}
