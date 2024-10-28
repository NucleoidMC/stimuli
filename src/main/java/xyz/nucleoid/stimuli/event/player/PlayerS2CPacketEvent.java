package xyz.nucleoid.stimuli.event.player;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when packet is send from server to {@link ServerPlayerEntity}
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and sends packet.
 * <li>{@link EventResult#DENY} cancels further processing and ignores the packet.
 * <li>{@link EventResult#PASS} moves on to the next listener.
 * </ul>
 */
public interface PlayerS2CPacketEvent {
    StimulusEvent<PlayerS2CPacketEvent> EVENT = StimulusEvent.create(PlayerS2CPacketEvent.class, ctx -> (sender, message) -> {
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
