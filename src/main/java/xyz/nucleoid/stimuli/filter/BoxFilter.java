package xyz.nucleoid.stimuli.filter;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xyz.nucleoid.stimuli.EventSource;

record BoxFilter(ResourceKey<Level> dimension, BlockPos min, BlockPos max) implements EventFilter {
    @Override
    public boolean accepts(EventSource source) {
        var pos = source.getPos();
        var dimension = source.getDimension();
        return (dimension == null || dimension == this.dimension) && (pos == null || this.containsPos(pos));
    }

    private boolean containsPos(BlockPos pos) {
        var min = this.min;
        var max = this.max;
        return pos.getX() >= min.getX() && pos.getY() >= min.getY() && pos.getZ() >= min.getZ()
                && pos.getX() <= max.getX() && pos.getY() <= max.getY() && pos.getZ() <= max.getZ();
    }
}
