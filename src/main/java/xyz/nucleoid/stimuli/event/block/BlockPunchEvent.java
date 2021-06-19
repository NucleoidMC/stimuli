package xyz.nucleoid.stimuli.event.block;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to punch a block.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the punch.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the punch.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the punch succeeds and the player could begin to break the block.
 */
public interface BlockPunchEvent {
    StimulusEvent<BlockPunchEvent> EVENT = StimulusEvent.create(BlockPunchEvent.class, ctx -> (puncher, direction, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPunchBlock(puncher, direction, pos);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onPunchBlock(ServerPlayerEntity puncher, Direction direction, BlockPos pos);
}
