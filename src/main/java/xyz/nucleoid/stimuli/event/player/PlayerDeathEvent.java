package xyz.nucleoid.stimuli.event.player;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} dies.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and kills the player.
 * <li>{@link ActionResult#FAIL} cancels further processing and does not kill the player.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the player is killed.
 */
public interface PlayerDeathEvent {
    StimulusEvent<PlayerDeathEvent> EVENT = StimulusEvent.create(PlayerDeathEvent.class, ctx -> (player, source) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDeath(player, source);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onDeath(ServerPlayerEntity player, DamageSource source);
}
