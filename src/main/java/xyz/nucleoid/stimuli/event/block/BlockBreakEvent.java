package xyz.nucleoid.stimuli.event.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayer} attempts to break a block.
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
    StimulusEvent<BlockBreakEvent> EVENT = StimulusEvent.create(BlockBreakEvent.class, ctx -> (player, level, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onBreak(player, level, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onBreak(ServerPlayer player, ServerLevel level, BlockPos pos);
}
