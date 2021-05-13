package xyz.nucleoid.stimuli.filter;

import xyz.nucleoid.stimuli.EventSource;

final class GlobalFilter implements EventFilter {
    public static final GlobalFilter INSTANCE = new GlobalFilter();

    private GlobalFilter() {
    }

    @Override
    public boolean accepts(EventSource source) {
        return true;
    }
}
