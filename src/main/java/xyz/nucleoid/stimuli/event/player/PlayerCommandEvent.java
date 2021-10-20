package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} executes a command through chat.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows executes the command.
 * <li>{@link ActionResult#FAIL} cancels further processing and prevents execution.
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * </ul>
 */
public interface PlayerCommandEvent {
    StimulusEvent<PlayerCommandEvent> EVENT = StimulusEvent.create(PlayerCommandEvent.class, ctx -> (player, command) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPlayerCommand(player, command);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onPlayerCommand(ServerPlayerEntity player, String command);
}
