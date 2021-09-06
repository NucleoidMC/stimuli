package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to regenerate health naturally.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the regeneration.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the regeneration.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the player successfully regenerates health.
 */
public interface PlayerRegenerateEvent {
    StimulusEvent<PlayerRegenerateEvent> EVENT = StimulusEvent.create(PlayerRegenerateEvent.class, ctx -> (player, amount) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onRegenerate(player, amount);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onRegenerate(ServerPlayerEntity player, float amount);
}
