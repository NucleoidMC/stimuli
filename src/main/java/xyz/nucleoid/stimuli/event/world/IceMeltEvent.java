package xyz.nucleoid.stimuli.event.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when ice attempts to be melted in the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further handlers and allows the ice to melt.
 * <li>{@link ActionResult#FAIL} cancels further handlers and does not allow the ice to melt.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 */
public interface IceMeltEvent {
    StimulusEvent<IceMeltEvent> EVENT = StimulusEvent.create(IceMeltEvent.class, ctx -> (world, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onIceMelt(world, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onIceMelt(ServerWorld world, BlockPos pos);
}
