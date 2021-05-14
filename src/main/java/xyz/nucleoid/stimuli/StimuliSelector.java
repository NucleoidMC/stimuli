package xyz.nucleoid.stimuli;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.event.StimulusEvent;
import xyz.nucleoid.stimuli.selector.EventListenerSelector;
import xyz.nucleoid.stimuli.selector.ListenerSelectorSet;

import java.util.Iterator;

public final class StimuliSelector {
    private final ListenerSelectorSet selectors;

    StimuliSelector(ListenerSelectorSet selectors) {
        this.selectors = selectors;
    }

    public EventInvokers at(World world, BlockPos pos) {
        return this.acquireInvokers(world.getServer(), EventSource.at(world, pos));
    }

    public EventInvokers forEntity(Entity entity) {
        return this.acquireInvokers(entity.getServer(), EventSource.forEntity(entity));
    }

    public EventInvokers forEntityAt(Entity entity, BlockPos pos) {
        return this.acquireInvokers(entity.getServer(), EventSource.forEntityAt(entity, pos));
    }

    EventInvokers acquireInvokers(MinecraftServer server, EventSource source) {
        Preconditions.checkNotNull(server, "missing server for event invokers");
        return SelectorEventInvokers.acquire(server, this, source);
    }

    <T> Iterator<T> selectListeners(MinecraftServer server, StimulusEvent<T> event, EventSource source) {
        return new ListenerIterator<>(server, event, source, this.selectors.getArray());
    }

    static final class ListenerIterator<T> extends AbstractIterator<T> {
        private final MinecraftServer server;
        private final StimulusEvent<T> event;
        private final EventSource source;
        private final EventListenerSelector[] selectors;

        private int selectorIndex;
        private Iterator<T> currentIterator;

        ListenerIterator(MinecraftServer server, StimulusEvent<T> event, EventSource source, EventListenerSelector[] selectors) {
            this.server = server;
            this.event = event;
            this.source = source;
            this.selectors = selectors;
        }

        @Override
        protected T computeNext() {
            EventListenerSelector[] selectors = this.selectors;
            Iterator<T> currentIterator = this.currentIterator;

            while (currentIterator == null || !currentIterator.hasNext()) {
                int index = this.selectorIndex++;
                if (index >= selectors.length) {
                    return this.endOfData();
                }

                EventListenerSelector selector = selectors[index];
                this.currentIterator = currentIterator = selector.selectListeners(this.server, this.event, this.source);
            }

            return currentIterator.next();
        }
    }
}
