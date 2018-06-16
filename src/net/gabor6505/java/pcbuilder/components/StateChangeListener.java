package net.gabor6505.java.pcbuilder.components;

import java.util.List;

public interface StateChangeListener {

    void loaded(String type, String displayName, boolean enabled, List<Component> affectedComponents);

    void reloaded(String type, String displayName, boolean enabled, List<Component> affectedComponents);

    void removed(String type);
}
