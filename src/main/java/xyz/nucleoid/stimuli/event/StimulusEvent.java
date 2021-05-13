package xyz.nucleoid.stimuli.event;

import com.google.common.base.Throwables;

import java.util.Collections;

public final class StimulusEvent<T> {
    private final Class<T> listenerType;
    private final EventInvokerFactory<T> invoker;

    private final T emptyInvoker;

    private StimulusEvent(Class<T> listenerType, EventInvokerFactory<T> invoker) {
        this.listenerType = listenerType;
        this.invoker = invoker;

        this.emptyInvoker = this.invoker.create(new EventInvokerContext<T>() {
            @Override
            public Iterable<T> getListeners() {
                return Collections.emptyList();
            }

            @Override
            public void handleException(Throwable throwable) {
                Throwables.throwIfUnchecked(throwable);
            }
        });
    }

    public static <T> StimulusEvent<T> create(Class<T> type, EventInvokerFactory<T> invoker) {
        return new StimulusEvent<>(type, invoker);
    }

    public T createInvoker(EventInvokerContext<T> context) {
        return this.invoker.create(context);
    }

    public T emptyInvoker() {
        return this.emptyInvoker;
    }

    public Class<T> getListenerType() {
        return this.listenerType;
    }
}
