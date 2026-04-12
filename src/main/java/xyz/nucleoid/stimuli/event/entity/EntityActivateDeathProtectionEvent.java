package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link LivingEntity} activates an item that provides death protection,
 * such as a totem of undying.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and activates the totem.
 * <li>{@link EventResult#DENY} cancels further processing and does not activate the totem.
 * <li>{@link EventResult#PASS} moves on to the next listener.
 * </ul>
 * </p>
 */
public interface EntityActivateDeathProtectionEvent {

    StimulusEvent<EntityActivateDeathProtectionEvent> EVENT = StimulusEvent.create(EntityActivateDeathProtectionEvent.class, ctx -> (entity, source, itemStack) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDeathProtectionActivate(entity, source, itemStack);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onDeathProtectionActivate(LivingEntity entity, DamageSource source, ItemStack itemStack);
}
