package xyz.nucleoid.stimuli.selector;

import net.minecraft.server.MinecraftServer;
import xyz.nucleoid.stimuli.EventSource;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.Iterator;

public interface EventListenerSelector {
    <T> Iterator<T> selectListeners(MinecraftServer server, StimulusEvent<T> event, EventSource source);
}
