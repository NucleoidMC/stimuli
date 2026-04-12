package xyz.nucleoid.stimuli;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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

    private ResourceKey<Level> dimension;
    private BlockPos pos;
    private Entity entity;

    private EventSource(ObjectPool<EventSource> pool) {
        super(pool);
    }

    void set(ResourceKey<Level> dimension, BlockPos pos, Entity entity) {
        this.dimension = dimension;
        this.pos = pos;
        this.entity = entity;
    }

    public static EventSource global() {
        return GLOBAL;
    }

    public static EventSource at(Level level, BlockPos pos) {
        return acquire(level.dimension(), pos, null);
    }

    public static EventSource at(ResourceKey<Level> dimension, BlockPos pos) {
        return acquire(dimension, pos, null);
    }

    public static EventSource allOf(Level level) {
        return acquire(level.dimension(), null, null);
    }

    public static EventSource allOf(ResourceKey<Level> dimension) {
        return acquire(dimension, null, null);
    }

    public static EventSource forEntity(Entity entity) {
        return acquire(entity.level().dimension(), entity.blockPosition(), entity);
    }

    public static EventSource forEntityAt(Entity entity, BlockPos pos) {
        return acquire(entity.level().dimension(), pos, entity);
    }

    public static EventSource forCommandSource(CommandSourceStack source) {
        return acquire(source.getLevel().dimension(), BlockPos.containing(source.getPosition()), source.getEntity());
    }

    static EventSource acquire(ResourceKey<Level> dimension, BlockPos pos, @Nullable Entity entity) {
        var source = POOL.acquire();
        source.set(dimension, pos, entity);
        return source;
    }

    @Nullable
    public ResourceKey<Level> getDimension() {
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
