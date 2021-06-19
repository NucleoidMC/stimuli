package xyz.nucleoid.stimuli.filter;

import xyz.nucleoid.stimuli.EventSource;

record AnyFilter(EventFilter... filters) implements EventFilter {
    @Override
    public boolean accepts(EventSource source) {
        for (var filter : this.filters) {
            if (filter.accepts(source)) {
                return true;
            }
        }
        return false;
    }
}
