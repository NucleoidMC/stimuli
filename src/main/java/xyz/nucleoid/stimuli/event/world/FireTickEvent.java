package xyz.nucleoid.stimuli.event.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when fire attempts to tick in the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further handlers and allows the fire to tick.
 * <li>{@link ActionResult#FAIL} cancels further handlers and does not allow the fire to tick.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 */
public interface FireTickEvent {
    StimulusEvent<FireTickEvent> EVENT = StimulusEvent.create(FireTickEvent.class, ctx -> (world, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onFireTick(world, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onFireTick(ServerWorld world, BlockPos pos);
}
