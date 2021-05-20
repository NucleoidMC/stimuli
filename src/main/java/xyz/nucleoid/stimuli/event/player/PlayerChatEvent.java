package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} sends a message in chat.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the message to be sent.
 * <li>{@link ActionResult#FAIL} cancels further processing and the message being sent.
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * </ul>
 */
public interface PlayerChatEvent {
    StimulusEvent<PlayerChatEvent> EVENT = StimulusEvent.create(PlayerChatEvent.class, ctx -> (sender, message) -> {
        try {
            for (PlayerChatEvent listener : ctx.getListeners()) {
                ActionResult result = listener.onSendChatMessage(sender, message);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onSendChatMessage(ServerPlayerEntity sender, Text message);
}
