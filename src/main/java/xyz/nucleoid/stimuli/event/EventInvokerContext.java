package xyz.nucleoid.stimuli.event;

public interface EventInvokerContext<T> {
    Iterable<T> getListeners();

    void handleException(Throwable throwable);
}
