package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to regenerate health naturally.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the regeneration.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the regeneration.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the player successfully regenerates health.
 */
public interface PlayerRegenerateEvent {
    StimulusEvent<PlayerRegenerateEvent> EVENT = StimulusEvent.create(PlayerRegenerateEvent.class, ctx -> (player, amount) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onRegenerate(player, amount);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onRegenerate(ServerPlayerEntity player, float amount);
}
