package xyz.nucleoid.stimuli.event.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public final class BlockPlaceEvent {
    /**
     * Called when any {@link ServerPlayer} attempts to place a block.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link EventResult#ALLOW} cancels further processing and allows the place.
     * <li>{@link EventResult#DENY} cancels further processing and cancels the place.
     * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link EventResult#PASS}, the place succeeds.
     */
    public static final StimulusEvent<Before> BEFORE = StimulusEvent.create(Before.class, ctx -> (player, level, pos, state, context) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPlace(player, level, pos, state, context);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    /**
     * Called after a {@link ServerPlayer} has placed a block.
     */
    public static final StimulusEvent<After> AFTER = StimulusEvent.create(After.class, ctx -> (player, level, pos, state) -> {
        try {
            for (var listener : ctx.getListeners()) {
                listener.onPlace(player, level, pos, state);
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
    });

    public interface Before {
        EventResult onPlace(ServerPlayer player, ServerLevel level, BlockPos pos, BlockState state, UseOnContext context);
    }

    public interface After {
        void onPlace(ServerPlayer player, ServerLevel level, BlockPos pos, BlockState state);
    }
}
