package xyz.nucleoid.stimuli.filter;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.EventSource;

/**
 * Represents a filter used for a {@link xyz.nucleoid.stimuli.selector.EventListenerSelector} to filter which events are
 * handled and which are ignored.
 * <p>
 * Filters operate on an {@link EventSource} which describes where the event originated from.
 */
public interface EventFilter {
    /**
     * Returns an event filter that accepts any event passing through it.
     *
     * @return the result event filter
     */
    static EventFilter global() {
        return GlobalFilter.INSTANCE;
    }

    /**
     * Returns an event filter that accepts only events from the given dimension.
     *
     * @param dimension the dimension to filter for
     * @return the result event filter
     */
    static EventFilter dimension(RegistryKey<World> dimension) {
        return new DimensionFilter(dimension);
    }

    /**
     * Returns an event filter that accepts only events from the given dimension and block bounds.
     *
     * @param dimension the dimension to filter for
     * @param min the minimum block coordinate to accept
     * @param max the maximum block coordinate to accept
     * @return the result event filter
     */
    static EventFilter box(RegistryKey<World> dimension, BlockPos min, BlockPos max) {
        return new BoxFilter(dimension, min, max);
    }

    /**
     * Returns an event filter that accepts events from any of the given filters.
     *
     * @param filters the filters to test
     * @return the result event filter
     */
    static EventFilter anyOf(EventFilter... filters) {
        return new AnyFilter(filters);
    }

    /**
     * Tests whether this filter accepts an event from the given {@link EventSource}
     *
     * @param source the origin of the tested event
     * @return {@code true} if this event should be accepted through this filter
     */
    boolean accepts(EventSource source);
}
