package xyz.nucleoid.stimuli.event.player;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayerEntity} attempts to attack another {@link Entity}.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the attack.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the attack.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the attack succeeds.
 */
public interface PlayerAttackEntityEvent {
    StimulusEvent<PlayerAttackEntityEvent> EVENT = StimulusEvent.create(PlayerAttackEntityEvent.class, ctx -> (attacker, hand, attacked, hitResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onAttackEntity(attacker, hand, attacked, hitResult);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onAttackEntity(ServerPlayerEntity attacker, Hand hand, Entity attacked, EntityHitResult hitResult);
}
