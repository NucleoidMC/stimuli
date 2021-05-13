package xyz.nucleoid.stimuli.selector;

import xyz.nucleoid.stimuli.EventSource;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.Iterator;

public interface EventListenerSelector {
    <T> Iterator<T> selectListeners(StimulusEvent<T> event, EventSource source);
}
