package xyz.nucleoid.stimuli.event.player;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} is damaged.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and damages the player.
 * <li>{@link ActionResult#FAIL} cancels further processing and does not damage the player.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the player is damaged as per normal behavior.
 */
public interface PlayerDamageEvent {
    StimulusEvent<PlayerDamageEvent> EVENT = StimulusEvent.create(PlayerDamageEvent.class, ctx -> (player, source, amount) -> {
        try {
            for (PlayerDamageEvent listener : ctx.getListeners()) {
                ActionResult result = listener.onDamage(player, source, amount);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onDamage(ServerPlayerEntity player, DamageSource source, float amount);
}
