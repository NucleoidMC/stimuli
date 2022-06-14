package xyz.nucleoid.stimuli.event.player;

import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} sends a message in chat. Message uses it's final formatting
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
            for (var listener : ctx.getListeners()) {
                var result = listener.onSendChatMessage(sender, message);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onSendChatMessage(MessageSender sender, SignedMessage message);
}
