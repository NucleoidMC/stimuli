package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a gravity-affected block attempts to fall.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the block to fall.
 * <li>{@link ActionResult#FAIL} cancels further processing and prevents the block from falling.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the block successfully falls.
 */
public interface BlockFallEvent {
    StimulusEvent<BlockFallEvent> EVENT = StimulusEvent.create(BlockFallEvent.class, ctx -> (world, pos, state) -> {
        try {
            for (BlockFallEvent listener : ctx.getListeners()) {
                ActionResult result = listener.onBlockFall(world, pos, state);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onBlockFall(ServerWorld world, BlockPos pos, BlockState state);
}
