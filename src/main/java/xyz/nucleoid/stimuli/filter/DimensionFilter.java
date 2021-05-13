package xyz.nucleoid.stimuli.filter;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.EventSource;

final class DimensionFilter implements EventFilter {
    private final RegistryKey<World> dimension;

    DimensionFilter(RegistryKey<World> dimension) {
        this.dimension = dimension;
    }

    @Override
    public boolean accepts(EventSource source) {
        RegistryKey<World> dimension = source.getDimension();
        return dimension == null || dimension == this.dimension;
    }
}
