package xyz.nucleoid.stimuli;

import com.google.common.base.Throwables;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.stimuli.event.EventInvokerContext;
import xyz.nucleoid.stimuli.event.StimulusEvent;
import xyz.nucleoid.stimuli.util.ObjectPool;
import xyz.nucleoid.stimuli.util.PooledObject;

import java.util.Iterator;
import java.util.Map;

final class SelectorEventInvokers extends PooledObject<SelectorEventInvokers> implements EventInvokers {
    private static final ObjectPool<SelectorEventInvokers> POOL = ObjectPool.create(1, SelectorEventInvokers::new);

    MinecraftServer server;
    StimuliSelector parent;
    EventSource source;

    private final Map<StimulusEvent<?>, Invoker<?>> invokers = new Reference2ObjectOpenHashMap<>();

    SelectorEventInvokers(ObjectPool<SelectorEventInvokers> owner) {
        super(owner);
    }

    static SelectorEventInvokers acquire(MinecraftServer server, StimuliSelector parent, EventSource source) {
        var invokers = POOL.acquire();
        invokers.setup(server, parent, source);
        return invokers;
    }

    void setup(MinecraftServer server, StimuliSelector parent, EventSource source) {
        this.server = server;
        this.parent = parent;
        this.source = source;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T get(StimulusEvent<T> event) {
        var invoker = (Invoker<T>) this.invokers.get(event);
        if (invoker == null) {
            invoker = new Invoker<>(event);
            this.invokers.put(event, invoker);
        }

        return invoker.invoker;
    }

    @Override
    protected void release() {
        this.source.close();
        this.source = null;
        this.server = null;
        this.parent = null;
    }

    final class Invoker<T> implements EventInvokerContext<T>, Iterable<T> {
        final StimulusEvent<T> event;
        final T invoker;

        Invoker(StimulusEvent<T> event) {
            this.event = event;
            this.invoker = event.createInvoker(this);
        }

        @Override
        public Iterable<T> getListeners() {
            return this;
        }

        @Override
        public Iterator<T> iterator() {
            var server = SelectorEventInvokers.this.server;
            var parent = SelectorEventInvokers.this.parent;
            var source = SelectorEventInvokers.this.source;
            return parent.selectListeners(server, this.event, source);
        }

        @Override
        public void handleException(Throwable throwable) {
            Throwables.throwIfUnchecked(throwable);
        }
    }
}
