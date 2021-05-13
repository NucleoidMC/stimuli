package xyz.nucleoid.stimuli.filter;

import xyz.nucleoid.stimuli.EventSource;

final class AnyFilter implements EventFilter {
    private final EventFilter[] filters;

    AnyFilter(EventFilter... filters) {
        this.filters = filters;
    }

    @Override
    public boolean accepts(EventSource source) {
        for (EventFilter filter : this.filters) {
            if (filter.accepts(source)) {
                return true;
            }
        }
        return false;
    }
}
