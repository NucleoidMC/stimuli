package xyz.nucleoid.stimuli.filter;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.EventSource;

record DimensionFilter(RegistryKey<World> dimension) implements EventFilter {
    @Override
    public boolean accepts(EventSource source) {
        var dimension = source.getDimension();
        return dimension == null || dimension == this.dimension;
    }
}
