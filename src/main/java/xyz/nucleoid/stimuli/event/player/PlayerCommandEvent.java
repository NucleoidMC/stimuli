package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} executes a command through chat.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows executes the command.
 * <li>{@link EventResult#DENY} cancels further processing and prevents execution.
 * <li>{@link EventResult#PASS} moves on to the next listener.
 * </ul>
 */
public interface PlayerCommandEvent {
    StimulusEvent<PlayerCommandEvent> EVENT = StimulusEvent.create(PlayerCommandEvent.class, ctx -> (player, command) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPlayerCommand(player, command);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onPlayerCommand(ServerPlayerEntity player, String command);
}
