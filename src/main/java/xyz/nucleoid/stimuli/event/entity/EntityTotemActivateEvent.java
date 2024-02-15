package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link LivingEntity} activates a totem of undying
 *
 * <p>Upon return;
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and activates the totem.
 * <li>{@link ActionResult#FAIL} cancels further processing and does not activate the totem.
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * </ul>
 * </p>
 */
public interface EntityTotemActivateEvent {

    StimulusEvent<EntityTotemActivateEvent> EVENT = StimulusEvent.create(EntityTotemActivateEvent.class, ctx -> (entity, source) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onTotem(entity, source);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onTotem(LivingEntity entity, DamageSource source);
}
