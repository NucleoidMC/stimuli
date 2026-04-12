package xyz.nucleoid.stimuli.event.player;

import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.Set;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

/**
 * Called when a {@link ServerPlayer} sends a message in chat. This event can be used to cancel a chat message
 * from being sent. Importantly, <em>the handler must distribute this chat message and/or its header.</em>
 * If the message is not correctly distributed, other clients' chat signing chains will detect a break and disconnect.
 * <p>
 * {@link net.minecraft.network.chat.OutgoingChatMessage} can be used to automatically dispatch headers to other players
 *
 * @see ServerPlayer#sendChatMessage(OutgoingChatMessage, boolean, ChatType.Bound)
 * @see net.minecraft.server.players.PlayerList#sendMessageHeader(SignedMessage, Set)
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

    boolean shouldConsumeChatMessage(ServerPlayer player, PlayerChatMessage message, ChatType.Bound messageType);
}
