package xyz.nucleoid.stimuli;

import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface EventInvokers extends AutoCloseable {
    @NotNull
    <T> T get(StimulusEvent<T> event);

    @Override
    void close();
}
