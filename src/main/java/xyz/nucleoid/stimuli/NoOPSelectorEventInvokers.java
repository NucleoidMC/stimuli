package xyz.nucleoid.stimuli;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.stimuli.event.EventInvokerContext;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.List;
import java.util.Map;

final class NoOPSelectorEventInvokers implements EventInvokers, EventInvokerContext<Object> {
    public static final NoOPSelectorEventInvokers INSTANCE = new NoOPSelectorEventInvokers();
    private final Map<StimulusEvent<Object>, Object> invokers = new Reference2ObjectOpenHashMap<>();

    NoOPSelectorEventInvokers() {}

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T get(StimulusEvent<T> event) {
        var invoker = (T) this.invokers.get(event);
        if (invoker == null) {
            invoker = event.createInvoker((EventInvokerContext<T>) this);
            this.invokers.put((StimulusEvent<Object>) event, invoker);
        }

        return invoker;
    }

    @Override
    public void close() {}

    @Override
    public Iterable<Object> getListeners() {
        return List.of();
    }

    @Override
    public void handleException(Throwable throwable) {

    }
}
