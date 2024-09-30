package xyz.nucleoid.stimuli.filter;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.EventSource;

public record CylinderFilter(RegistryKey<World> dimension, BlockPos center, int radius, int height) implements EventFilter {
    @Override
    public boolean accepts(EventSource source) {
        var pos = source.getPos();
        var dimension = source.getDimension();
        return (dimension == null || dimension == this.dimension) && (pos == null || this.containsPos(pos));
    }

    private boolean containsPos(BlockPos pos) {
        var center = this.center;
        var radius = this.radius;
        var height = this.height;
        var dx = pos.getX() - center.getX();
        var dz = pos.getZ() - center.getZ();
        return dx * dx + dz * dz <= radius * radius && pos.getY() >= center.getY() && pos.getY() <= center.getY() + height;
    }
}
