package xyz.nucleoid.stimuli.event.player;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} sends a message in chat. Message uses its final formatting
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the message to be sent.
 * <li>{@link EventResult#DENY} cancels further processing and the message being sent.
 * <li>{@link EventResult#PASS} moves on to the next listener.
 * </ul>
 *
 * @see ReplacePlayerChatEvent to cancel and modify a chat message
 */
public interface PlayerChatEvent {
    StimulusEvent<PlayerChatEvent> EVENT = StimulusEvent.create(PlayerChatEvent.class, ctx -> (player, message, messageType) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSendChatMessage(player, message, messageType);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onSendChatMessage(ServerPlayerEntity player, SignedMessage message, MessageType.Parameters messageType);
}
