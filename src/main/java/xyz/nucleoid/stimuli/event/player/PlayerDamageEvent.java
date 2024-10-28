package xyz.nucleoid.stimuli.event.player;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} is damaged.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and damages the player.
 * <li>{@link EventResult#DENY} cancels further processing and does not damage the player.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the player is damaged as per normal behavior.
 */
public interface PlayerDamageEvent {
    StimulusEvent<PlayerDamageEvent> EVENT = StimulusEvent.create(PlayerDamageEvent.class, ctx -> (player, source, amount) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDamage(player, source, amount);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onDamage(ServerPlayerEntity player, DamageSource source, float amount);
}
