package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} sends a message, before it being formatted.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the message to be sent.
 * <li>{@link ActionResult#FAIL} cancels further processing and the prevents the message from being sent.
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * </ul>
 */
public interface PlayerChatContentEvent {
    StimulusEvent<PlayerChatContentEvent> EVENT = StimulusEvent.create(PlayerChatContentEvent.class, ctx -> (sender, message) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSendChat(sender, message);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onSendChat(ServerPlayerEntity sender, MutableMessage message);

    final class MutableMessage {
        private String raw;
        private String filtered;

        public MutableMessage(String raw, String filtered) {
            this.raw = raw;
            this.filtered = filtered;
        }

        public String getRaw() {
            return this.raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }

        public String getFiltered() {
            return this.filtered;
        }

        public void setFiltered(String filtered) {
            this.filtered = filtered;
        }

        public void set(String message) {
            this.setRaw(message);
            this.setFiltered(message);
        }
    }
}
