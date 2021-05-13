package xyz.nucleoid.stimuli.selector;

import java.util.ArrayList;
import java.util.List;

public final class ListenerSelectorSet {
    private final List<EventListenerSelector> selectors = new ArrayList<>();
    private EventListenerSelector[] array = new EventListenerSelector[0];

    public boolean add(EventListenerSelector selector) {
        if (!this.selectors.contains(selector)) {
            this.selectors.add(selector);
            this.updateArray();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(EventListenerSelector selector) {
        if (this.selectors.remove(selector)) {
            this.updateArray();
            return true;
        } else {
            return false;
        }
    }

    private void updateArray() {
        this.array = this.selectors.toArray(new EventListenerSelector[0]);
    }

    public EventListenerSelector[] getArray() {
        return this.array;
    }
}
