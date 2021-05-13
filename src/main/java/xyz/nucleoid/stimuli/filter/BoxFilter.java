package xyz.nucleoid.stimuli.filter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.EventSource;

final class BoxFilter implements EventFilter {
    private final RegistryKey<World> dimension;
    private final BlockPos min;
    private final BlockPos max;

    BoxFilter(RegistryKey<World> dimension, BlockPos min, BlockPos max) {
        this.dimension = dimension;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean accepts(EventSource source) {
        BlockPos pos = source.getPos();
        RegistryKey<World> dimension = source.getDimension();
        return (dimension == null || dimension == this.dimension) && (pos == null || this.containsPos(pos));
    }

    private boolean containsPos(BlockPos pos) {
        BlockPos min = this.min;
        BlockPos max = this.max;
        return pos.getX() >= min.getX() && pos.getY() >= min.getY() && pos.getZ() >= min.getZ()
                && pos.getX() <= max.getX() && pos.getY() <= max.getY() && pos.getZ() <= max.getZ();
    }
}
