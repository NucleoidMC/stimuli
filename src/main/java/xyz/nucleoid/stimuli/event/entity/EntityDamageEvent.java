package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link LivingEntity} is damaged.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and damages the entity.
 * <li>{@link EventResult#DENY} cancels further processing and does not damage the entity.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the entity is damaged as per normal behavior.
 */
public interface EntityDamageEvent {
    StimulusEvent<EntityDamageEvent> EVENT = StimulusEvent.create(EntityDamageEvent.class, ctx -> (entity, source, amount) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDamage(entity, source, amount);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onDamage(LivingEntity entity, DamageSource source, float amount);
}
