package xyz.nucleoid.stimuli.event.block;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayerEntity} attempts to break a block.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the break.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the break.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the break succeeds.
 */
public interface BlockBreakEvent {
    StimulusEvent<BlockBreakEvent> EVENT = StimulusEvent.create(BlockBreakEvent.class, ctx -> (player, world, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onBreak(player, world, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onBreak(ServerPlayerEntity player, ServerWorld world, BlockPos pos);
}
