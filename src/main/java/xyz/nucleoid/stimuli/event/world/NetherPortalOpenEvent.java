package xyz.nucleoid.stimuli.event.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a nether portal is attempted to be opened within the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the portal to be opened.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the portal to be opened.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface NetherPortalOpenEvent {
    StimulusEvent<NetherPortalOpenEvent> EVENT = StimulusEvent.create(NetherPortalOpenEvent.class, ctx -> (world, lowerCorner) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onOpenNetherPortal(world, lowerCorner);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onOpenNetherPortal(ServerWorld world, BlockPos lowerCorner);
}
