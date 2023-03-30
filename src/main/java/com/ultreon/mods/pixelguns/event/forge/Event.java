package com.ultreon.mods.pixelguns.event.forge;

import java.util.ArrayList;
import java.util.HashMap;

public class Event {

    private static final HashMap<Class<? extends Event>, ArrayList<EventHandler<?>>> HANDLERS = new HashMap<>();

    public Event() {
    }

    public static <T extends Event> void registerHandler(Class<T> type, EventHandler<T> handler) {
        HANDLERS.computeIfAbsent(type, k -> new ArrayList<>());
        HANDLERS.get(type).add(handler);
    }

    public static <T extends Event> void call(T event) {
        if (!HANDLERS.containsKey(event.getClass())) {
            return;
        }

        for (EventHandler handler : HANDLERS.get(event.getClass())) {
            handler.onEvent(event);
        }
    }
}