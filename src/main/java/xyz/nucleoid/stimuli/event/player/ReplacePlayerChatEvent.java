package xyz.nucleoid.stimuli.event.player;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.Set;

/**
 * Called when a {@link ServerPlayerEntity} sends a message in chat. This event can be used to cancel a chat message
 * from being sent. Importantly, <em>the handler must distribute this chat message and/or its header.</em>
 * If the message is not correctly distributed, other clients' chat signing chains will detect a break and disconnect.
 * <p>
 * {@link net.minecraft.network.message.SentMessage} can be used to automatically dispatch headers to other players
 *
 * @see ServerPlayerEntity#sendChatMessage(SentMessage, boolean, MessageType.Parameters)
 * @see net.minecraft.server.PlayerManager#sendMessageHeader(SignedMessage, Set)
 * @see PlayerChatEvent to cancel chat events without modification
 */
public interface ReplacePlayerChatEvent {
    StimulusEvent<ReplacePlayerChatEvent> EVENT = StimulusEvent.create(ReplacePlayerChatEvent.class, ctx -> (player, message, messageType) -> {
        try {
            for (var listener : ctx.getListeners()) {
                if (listener.shouldConsumeChatMessage(player, message, messageType)) {
                    return true;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return false;
    });

    boolean shouldConsumeChatMessage(ServerPlayerEntity player, SignedMessage message, MessageType.Parameters messageType);
}
