package xyz.nucleoid.stimuli.event.player;

import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} sends a packets to server
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the packet to be processed.
 * <li>{@link ActionResult#FAIL} cancels further processing and ignores the packet.
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * </ul>
 */
public interface PlayerC2SPacketEvent {
    StimulusEvent<PlayerC2SPacketEvent> EVENT = StimulusEvent.create(PlayerC2SPacketEvent.class, ctx -> (sender, message) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPacket(sender, message);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onPacket(ServerPlayerEntity player, Packet<?> packet);
}
