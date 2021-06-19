package xyz.nucleoid.stimuli.event;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class EventListenerMap implements EventRegistrar {
    private final Reference2ObjectMap<StimulusEvent<?>, List<Object>> listeners = new Reference2ObjectOpenHashMap<>();

    @Override
    public <T> void listen(StimulusEvent<T> event, T listener) {
        this.listeners.computeIfAbsent(event, e -> new ArrayList<>()).add(listener);
    }

    @Override
    public <T> void unlisten(StimulusEvent<T> event, T listener) {
        var listeners = this.listeners.get(event);
        if (listeners != null && listeners.remove(listener)) {
            if (listeners.isEmpty()) {
                this.listeners.remove(event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> get(StimulusEvent<T> event) {
        return (Collection<T>) this.listeners.getOrDefault(event, Collections.emptyList());
    }

    public Set<StimulusEvent<?>> getEvents() {
        return this.listeners.keySet();
    }
}
