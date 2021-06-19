package xyz.nucleoid.stimuli.event.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a nether portal is attempted to be opened within the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further handlers and allows the portal to be opened.
 * <li>{@link ActionResult#FAIL} cancels further handlers and does not allow the portal to be opened.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 */
public interface NetherPortalOpenEvent {
    StimulusEvent<NetherPortalOpenEvent> EVENT = StimulusEvent.create(NetherPortalOpenEvent.class, ctx -> (world, lowerCorner) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onOpenNetherPortal(world, lowerCorner);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onOpenNetherPortal(ServerWorld world, BlockPos lowerCorner);
}
