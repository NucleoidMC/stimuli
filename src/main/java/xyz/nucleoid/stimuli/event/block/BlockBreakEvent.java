package xyz.nucleoid.stimuli.event.block;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayerEntity} attempts to break a block.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the break.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the break.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the break succeeds.
 */
public interface BlockBreakEvent {
    StimulusEvent<BlockBreakEvent> EVENT = StimulusEvent.create(BlockBreakEvent.class, ctx -> (player, world, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onBreak(player, world, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onBreak(ServerPlayerEntity player, ServerWorld world, BlockPos pos);
}
