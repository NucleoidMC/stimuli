package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link LivingEntity} activates a totem of undying.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and activates the totem.
 * <li>{@link ActionResult#FAIL} cancels further processing and does not activate the totem.
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * </ul>
 * </p>
 */
public interface EntityActivateTotemEvent {

    StimulusEvent<EntityActivateTotemEvent> EVENT = StimulusEvent.create(EntityActivateTotemEvent.class, ctx -> (entity, source, itemStack) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onTotemActivate(entity, source, itemStack);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onTotemActivate(LivingEntity entity, DamageSource source, ItemStack itemStack);
}
