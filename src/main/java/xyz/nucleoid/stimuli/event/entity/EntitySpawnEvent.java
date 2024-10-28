package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called before {@link LivingEntity} is spawned.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and spawns the entity.
 * <li>{@link EventResult#DENY} cancels further processing and does doesn't spawn the entity.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the entity is killed.
 */
public interface EntitySpawnEvent {
    StimulusEvent<EntitySpawnEvent> EVENT = StimulusEvent.create(EntitySpawnEvent.class, ctx -> (entity) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSpawn(entity);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onSpawn(Entity entity);
}
