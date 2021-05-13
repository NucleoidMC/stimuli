package xyz.nucleoid.stimuli.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class BlockEvents {
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
    public static final StimulusEvent<Break> BREAK = StimulusEvent.create(Break.class, ctx -> (player, pos) -> {
        try {
            for (Break listener : ctx.getListeners()) {
                ActionResult result = listener.onBreak(player, pos);
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
    public static final StimulusEvent<Place> PLACE = StimulusEvent.create(Place.class, ctx -> (player, pos, state, context) -> {
        try {
            for (Place listener : ctx.getListeners()) {
                ActionResult result = listener.onPlace(player, pos, state, context);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<DropItems> DROP_ITEMS = StimulusEvent.create(DropItems.class, ctx -> (player, pos, state, dropStacks) -> {
        try {
            for (DropItems listener : ctx.getListeners()) {
                TypedActionResult<List<ItemStack>> result = listener.onDropItems(player, pos, state, dropStacks);
                dropStacks = result.getValue();
                if (result.getResult() != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return TypedActionResult.pass(dropStacks);
    });

    public interface Break {
        ActionResult onBreak(ServerPlayerEntity player, BlockPos pos);
    }

    public interface Place {
        ActionResult onPlace(ServerPlayerEntity player, BlockPos pos, BlockState state, ItemUsageContext context);
    }

    public interface DropItems {
        TypedActionResult<List<ItemStack>> onDropItems(@Nullable Entity breaker, BlockPos pos, BlockState state, List<ItemStack> dropStacks);
    }
}
