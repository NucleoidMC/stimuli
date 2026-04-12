package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayer} dies.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and kills the player.
 * <li>{@link EventResult#DENY} cancels further processing and does not kill the player.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the player is killed.
 */
public interface PlayerDeathEvent {
    StimulusEvent<PlayerDeathEvent> EVENT = StimulusEvent.create(PlayerDeathEvent.class, ctx -> (player, source) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDeath(player, source);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onDeath(ServerPlayer player, DamageSource source);
}
