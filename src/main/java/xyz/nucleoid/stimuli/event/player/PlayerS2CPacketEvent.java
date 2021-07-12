package xyz.nucleoid.stimuli.event.player;

import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when packet is send from server to {@link ServerPlayerEntity}
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and sends packet.
 * <li>{@link ActionResult#FAIL} cancels further processing and ignores the packet.
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * </ul>
 */
public interface PlayerS2CPacketEvent {
    StimulusEvent<PlayerS2CPacketEvent> EVENT = StimulusEvent.create(PlayerS2CPacketEvent.class, ctx -> (sender, message) -> {
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
