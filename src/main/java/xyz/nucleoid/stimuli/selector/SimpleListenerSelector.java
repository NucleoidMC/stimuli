package xyz.nucleoid.stimuli.selector;

import net.minecraft.server.MinecraftServer;
import xyz.nucleoid.stimuli.EventSource;
import xyz.nucleoid.stimuli.event.EventListenerMap;
import xyz.nucleoid.stimuli.event.StimulusEvent;
import xyz.nucleoid.stimuli.filter.EventFilter;

import java.util.Collections;
import java.util.Iterator;

public record SimpleListenerSelector(EventFilter filter, EventListenerMap listeners) implements EventListenerSelector {
    @Override
    public <T> Iterator<T> selectListeners(MinecraftServer server, StimulusEvent<T> event, EventSource source) {
        if (this.filter.accepts(source)) {
            return this.listeners.get(event).iterator();
        } else {
            return Collections.emptyIterator();
        }
    }
}
