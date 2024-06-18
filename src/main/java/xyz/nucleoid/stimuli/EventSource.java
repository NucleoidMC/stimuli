package xyz.nucleoid.stimuli;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.filter.EventFilter;
import xyz.nucleoid.stimuli.util.ObjectPool;
import xyz.nucleoid.stimuli.util.PooledObject;

/**
 * Represents the location that an event originated from. Used to filter events with an {@link EventFilter}.
 * This involves a dimension and position, as well as a responsible entity. None of the dimension, position, nor entity
 * are required to be present. An absent dimension or position implies the source originated from "everywhere",
 * and an absent entity implies no entity was responsible for the given event.
 * <p>
 * It is important to note that {@link EventSource} instances are pooled! They should be closed with
 * {@link EventSource#close()} when no longer in use in order to ensure they do not get leaked.
 */
public final class EventSource extends PooledObject<EventSource> {
    private static final ObjectPool<EventSource> POOL = ObjectPool.create(16, EventSource::new);

    private static final EventSource GLOBAL = new EventSource(null);

    private RegistryKey<World> dimension;
    private BlockPos pos;
    private Entity entity;

    private EventSource(ObjectPool<EventSource> pool) {
        super(pool);
    }

    void set(RegistryKey<World> dimension, BlockPos pos, Entity entity) {
        this.dimension = dimension;
        this.pos = pos;
        this.entity = entity;
    }

    public static EventSource global() {
        return GLOBAL;
    }

    public static EventSource at(World world, BlockPos pos) {
        return acquire(world.getRegistryKey(), pos, null);
    }

    public static EventSource at(RegistryKey<World> dimension, BlockPos pos) {
        return acquire(dimension, pos, null);
    }

    public static EventSource allOf(World world) {
        return acquire(world.getRegistryKey(), null, null);
    }

    public static EventSource allOf(RegistryKey<World> dimension) {
        return acquire(dimension, null, null);
    }

    public static EventSource forEntity(Entity entity) {
        return acquire(entity.getWorld().getRegistryKey(), entity.getBlockPos(), entity);
    }

    public static EventSource forEntityAt(Entity entity, BlockPos pos) {
        return acquire(entity.getWorld().getRegistryKey(), pos, entity);
    }

    public static EventSource forCommandSource(ServerCommandSource source) {
        return acquire(source.getWorld().getRegistryKey(), BlockPos.ofFloored(source.getPosition()), source.getEntity());
    }

    static EventSource acquire(RegistryKey<World> dimension, BlockPos pos, @Nullable Entity entity) {
        var source = POOL.acquire();
        source.set(dimension, pos, entity);
        return source;
    }

    @Nullable
    public RegistryKey<World> getDimension() {
        return this.dimension;
    }

    @Nullable
    public BlockPos getPos() {
        return this.pos;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    protected void release() {
        this.set(null, null, null);
    }
}
