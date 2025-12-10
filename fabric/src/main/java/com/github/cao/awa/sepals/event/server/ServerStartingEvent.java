package com.github.cao.awa.sepals.event.server;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ServerStartingEvent {
    private static final List<Consumer<MinecraftServer>> listeners = new ArrayList<>();

    public static void listen(Consumer<MinecraftServer> listener) {
        ServerStartingEvent.listeners.add(listener);
    }

    public static void trigger(MinecraftServer server) {
        for (Consumer<MinecraftServer> listener : ServerStartingEvent.listeners) {
            listener.accept(server);
        }
    }
}
