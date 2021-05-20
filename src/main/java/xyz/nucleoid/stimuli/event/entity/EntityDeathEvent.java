package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link LivingEntity} dies.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and kills the entity.
 * <li>{@link ActionResult#FAIL} cancels further processing and does not kill the entity.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the entity is killed.
 */
public interface EntityDeathEvent {
    StimulusEvent<EntityDeathEvent> EVENT = StimulusEvent.create(EntityDeathEvent.class, ctx -> (entity, source) -> {
        try {
            for (EntityDeathEvent listener : ctx.getListeners()) {
                ActionResult result = listener.onDeath(entity, source);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onDeath(LivingEntity entity, DamageSource source);
}
