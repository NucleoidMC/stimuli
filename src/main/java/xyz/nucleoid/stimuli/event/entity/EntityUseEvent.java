package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to use an entity by interacting.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the use.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the use.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the use succeeds and proceeds with normal logic.
 */
public interface EntityUseEvent {
    StimulusEvent<EntityUseEvent> EVENT = StimulusEvent.create(EntityUseEvent.class, ctx -> {
        return (player, entity, hand, hitResult) -> {
            try {
                for (var listener : ctx.getListeners()) {
                    var result = listener.onUse(player, entity, hand, hitResult);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return ActionResult.PASS;
        };
    });

    ActionResult onUse(ServerPlayerEntity player, Entity entity, Hand hand, EntityHitResult hitResult);
}
