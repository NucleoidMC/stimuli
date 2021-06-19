package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} loses hunger.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the player to lose hunger.
 * <li>{@link ActionResult#FAIL} cancels further processing and prevents the player from losing hunger.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 */
public interface PlayerConsumeHungerEvent {
    StimulusEvent<PlayerConsumeHungerEvent> EVENT = StimulusEvent.create(PlayerConsumeHungerEvent.class, ctx -> (player, foodLevel, saturation, exhaustion) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onConsumeHunger(player, foodLevel, saturation, exhaustion);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onConsumeHunger(ServerPlayerEntity player, int foodLevel, float saturation, float exhaustion);
}
