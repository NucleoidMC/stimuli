package xyz.nucleoid.stimuli.event.player;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} sends a packets to server
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the packet to be processed.
 * <li>{@link EventResult#DENY} cancels further processing and ignores the packet.
 * <li>{@link EventResult#PASS} moves on to the next listener.
 * </ul>
 */
public interface PlayerC2SPacketEvent {
    StimulusEvent<PlayerC2SPacketEvent> EVENT = StimulusEvent.create(PlayerC2SPacketEvent.class, ctx -> (sender, message) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPacket(sender, message);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onPacket(ServerPlayerEntity player, Packet<?> packet);
}
