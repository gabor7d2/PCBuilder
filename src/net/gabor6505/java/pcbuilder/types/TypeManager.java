package net.gabor6505.java.pcbuilder.types;

import java.util.*;

public class TypeManager {

    private final static Map<String, ReloadListener> listeners = new HashMap<>();
    private final static Map<Integer, List<ReloadListener>> listenerPriorities = new HashMap<>();

    public static void addReloadListener(String type, ReloadListener l, int priority) {
        listeners.put(type, l);

        if (!listenerPriorities.containsKey(priority)) {
            listenerPriorities.put(priority, new ArrayList<>());
        }
        listenerPriorities.get(priority).add(l);
    }

    public static void reload() {
        List<Integer> priorities = new ArrayList<>(listenerPriorities.keySet());
        Collections.sort(priorities);

        for (int priority : priorities) {
            for (ReloadListener l : listenerPriorities.get(priority)) {
                l.reload();
            }
        }
    }

    public interface ReloadListener {

        void reload();
    }
}
