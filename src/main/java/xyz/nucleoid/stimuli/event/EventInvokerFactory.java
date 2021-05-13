package xyz.nucleoid.stimuli.event;

public interface EventInvokerFactory<T> {
    T create(EventInvokerContext<T> context);
}
