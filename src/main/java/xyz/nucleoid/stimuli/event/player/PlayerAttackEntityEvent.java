package xyz.nucleoid.stimuli.event.player;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayerEntity} attempts to attack another {@link Entity}.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the attack.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the attack.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the attack succeeds.
 */
public interface PlayerAttackEntityEvent {
    StimulusEvent<PlayerAttackEntityEvent> EVENT = StimulusEvent.create(PlayerAttackEntityEvent.class, ctx -> (attacker, hand, attacked, hitResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onAttackEntity(attacker, hand, attacked, hitResult);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onAttackEntity(ServerPlayerEntity attacker, Hand hand, Entity attacked, EntityHitResult hitResult);
}
