package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a block attempts to randomly tick in the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the block to tick.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the block to tick.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface BlockRandomTickEvent {
    StimulusEvent<BlockRandomTickEvent> EVENT = StimulusEvent.create(BlockRandomTickEvent.class, ctx -> (world, pos, state) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onBlockRandomTick(world, pos, state);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onBlockRandomTick(ServerWorld world, BlockPos pos, BlockState state);
}
