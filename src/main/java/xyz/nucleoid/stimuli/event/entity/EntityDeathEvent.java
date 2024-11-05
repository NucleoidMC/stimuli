package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link LivingEntity} dies.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and kills the entity.
 * <li>{@link EventResult#DENY} cancels further processing and does not kill the entity.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the entity is killed.
 */
public interface EntityDeathEvent {
    StimulusEvent<EntityDeathEvent> EVENT = StimulusEvent.create(EntityDeathEvent.class, ctx -> (entity, source) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDeath(entity, source);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onDeath(LivingEntity entity, DamageSource source);
}
