package xyz.nucleoid.stimuli.event.player;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} sends a message in chat. Message uses its final formatting
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the message to be sent.
 * <li>{@link ActionResult#FAIL} cancels further processing and the message being sent.
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * </ul>
 *
 * @see ReplacePlayerChatEvent to cancel and modify a chat message
 */
public interface PlayerChatEvent {
    StimulusEvent<PlayerChatEvent> EVENT = StimulusEvent.create(PlayerChatEvent.class, ctx -> (player, message, messageType) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSendChatMessage(player, message, messageType);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onSendChatMessage(ServerPlayerEntity player, SignedMessage message, MessageType.Parameters messageType);
}
