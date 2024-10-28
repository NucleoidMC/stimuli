package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayerEntity} attempts to switch items in offhand
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the action.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the action.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the attack succeeds.
 *
 * @since 0.2.7
 */
public interface PlayerSwapWithOffhandEvent {
    StimulusEvent<PlayerSwapWithOffhandEvent> EVENT = StimulusEvent.create(PlayerSwapWithOffhandEvent.class, ctx -> (player) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSwapWithOffhand(player);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onSwapWithOffhand(ServerPlayerEntity player);
}
