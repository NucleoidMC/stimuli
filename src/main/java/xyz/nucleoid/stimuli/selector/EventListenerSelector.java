package xyz.nucleoid.stimuli.selector;

import net.minecraft.server.MinecraftServer;
import xyz.nucleoid.stimuli.EventSource;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.Iterator;

/**
 * Handles a lookup from a {@link StimulusEvent} and {@link EventSource} to return an iterator of event listeners.
 * The listener selector is responsible for processing any filtering logic based on the given {@link EventSource}.
 *
 * @see SimpleListenerSelector
 */
public interface EventListenerSelector {
    <T> Iterator<T> selectListeners(MinecraftServer server, StimulusEvent<T> event, EventSource source);
}
