package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public final class BlockPlaceEvent {
    /**
     * Called when any {@link ServerPlayerEntity} attempts to place a block.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the place.
     * <li>{@link ActionResult#FAIL} cancels further processing and cancels the place.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the place succeeds.
     */
    public static final StimulusEvent<Before> BEFORE = StimulusEvent.create(Before.class, ctx -> (player, world, pos, state, context) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPlace(player, world, pos, state, context);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    /**
     * Called after a {@link ServerPlayerEntity} has placed a block.
     */
    public static final StimulusEvent<After> AFTER = StimulusEvent.create(After.class, ctx -> (player, world, pos, state) -> {
        try {
            for (var listener : ctx.getListeners()) {
                listener.onPlace(player, world, pos, state);
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
    });

    public interface Before {
        ActionResult onPlace(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state, ItemUsageContext context);
    }

    public interface After {
        void onPlace(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state);
    }
}
