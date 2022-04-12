package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayerEntity} attempts to switch items in offhand
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the action.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the action.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the attack succeeds.
 *
 * @since 0.2.7
 */
public interface PlayerSwapWithOffhandEvent {
    StimulusEvent<PlayerSwapWithOffhandEvent> EVENT = StimulusEvent.create(PlayerSwapWithOffhandEvent.class, ctx -> (player) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSwapWithOffhand(player);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onSwapWithOffhand(ServerPlayerEntity player);
}
