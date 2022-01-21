package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link LivingEntity} is damaged.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and damages the entity.
 * <li>{@link ActionResult#FAIL} cancels further processing and does not damage the entity.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the entity is damaged as per normal behavior.
 */
public interface EntityDamageEvent {
    StimulusEvent<EntityDamageEvent> EVENT = StimulusEvent.create(EntityDamageEvent.class, ctx -> (entity, source, amount) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDamage(entity, source, amount);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onDamage(LivingEntity entity, DamageSource source, float amount);
}
