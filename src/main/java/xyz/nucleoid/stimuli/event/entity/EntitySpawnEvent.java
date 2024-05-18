package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called before {@link LivingEntity} is spawned.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and spawns the entity.
 * <li>{@link ActionResult#FAIL} cancels further processing and does doesn't spawn the entity.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the entity is killed.
 */
public interface EntitySpawnEvent {
    StimulusEvent<EntitySpawnEvent> EVENT = StimulusEvent.create(EntitySpawnEvent.class, ctx -> (entity) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSpawn(entity);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onSpawn(Entity entity);
}
