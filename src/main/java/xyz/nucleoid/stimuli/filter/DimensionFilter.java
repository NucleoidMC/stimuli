package xyz.nucleoid.stimuli.filter;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xyz.nucleoid.stimuli.EventSource;

record DimensionFilter(ResourceKey<Level> dimension) implements EventFilter {
    @Override
    public boolean accepts(EventSource source) {
        var dimension = source.getDimension();
        return dimension == null || dimension == this.dimension;
    }
}
