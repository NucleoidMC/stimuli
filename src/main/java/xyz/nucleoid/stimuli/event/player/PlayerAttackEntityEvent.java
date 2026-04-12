package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayer} attempts to attack another {@link Entity}.
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

    EventResult onAttackEntity(ServerPlayer attacker, InteractionHand hand, Entity attacked, EntityHitResult hitResult);
}
