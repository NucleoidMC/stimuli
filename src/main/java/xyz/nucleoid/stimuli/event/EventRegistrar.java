package xyz.nucleoid.stimuli.event;

public interface EventRegistrar {
    <T> void listen(StimulusEvent<T> event, T listener);

    <T> void unlisten(StimulusEvent<T> event, T listener);
}
