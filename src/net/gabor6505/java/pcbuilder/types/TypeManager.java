package net.gabor6505.java.pcbuilder.types;

import java.util.HashMap;
import java.util.Map;

public class TypeManager {

    private final static Map<String, ReloadListener> listeners = new HashMap<>();

    public static void addReloadListener(String type, ReloadListener l) {
        listeners.put(type, l);
    }

    public static void removeReloadListener(String type) {
        listeners.remove(type);
    }

    public static void reload() {
        for (ReloadListener l : listeners.values()) {
            l.reload();
        }
    }
}
