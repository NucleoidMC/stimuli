package xyz.nucleoid.stimuli;

import xyz.nucleoid.stimuli.event.EventListenerMap;
import xyz.nucleoid.stimuli.event.EventRegistrar;
import xyz.nucleoid.stimuli.event.StimulusEvent;
import xyz.nucleoid.stimuli.selector.EventListenerSelector;
import xyz.nucleoid.stimuli.selector.ListenerSelectorSet;

import java.util.Iterator;

public final class Stimuli {
    private static final ListenerSelectorSet SELECTORS = new ListenerSelectorSet();

    private static final StimuliSelector SELECT = new StimuliSelector(SELECTORS);

    private static final Global GLOBAL = new Global();

    static {
        SELECTORS.add(GLOBAL.selector);
    }

    private Stimuli() {
    }

    public static Global global() {
        return GLOBAL;
    }

    public static StimuliSelector select() {
        return SELECT;
    }

    public static boolean registerSelector(EventListenerSelector selector) {
        return SELECTORS.add(selector);
    }

    public static boolean unregisterSelector(EventListenerSelector selector) {
        return SELECTORS.remove(selector);
    }

    public static final class Global implements EventRegistrar {
        private final EventListenerMap listeners = new EventListenerMap();
        private final Selector selector = new Selector(this.listeners);

        @Override
        public <T> void listen(StimulusEvent<T> event, T listener) {
            this.listeners.listen(event, listener);
        }

        @Override
        public <T> void unlisten(StimulusEvent<T> event, T listener) {
            this.listeners.unlisten(event, listener);
        }

        static final class Selector implements EventListenerSelector {
            private final EventListenerMap listeners;

            Selector(EventListenerMap listeners) {
                this.listeners = listeners;
            }

            @Override
            public <T> Iterator<T> selectListeners(StimulusEvent<T> event, EventSource source) {
                return this.listeners.get(event).iterator();
            }
        }
    }
}
