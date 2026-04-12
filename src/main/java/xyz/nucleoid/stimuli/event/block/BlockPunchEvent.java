package xyz.nucleoid.stimuli.event.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayer} attempts to punch a block.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the punch.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the punch.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the punch succeeds and the player could begin to break the block.
 */
public interface BlockPunchEvent {
    StimulusEvent<BlockPunchEvent> EVENT = StimulusEvent.create(BlockPunchEvent.class, ctx -> (puncher, direction, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPunchBlock(puncher, direction, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onPunchBlock(ServerPlayer puncher, Direction direction, BlockPos pos);
}
