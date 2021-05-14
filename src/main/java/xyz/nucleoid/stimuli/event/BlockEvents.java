package xyz.nucleoid.stimuli.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
    public static final StimulusEvent<Break> BREAK = StimulusEvent.create(Break.class, ctx -> (player, world, pos) -> {
        try {
            for (Break listener : ctx.getListeners()) {
                ActionResult result = listener.onBreak(player, world, pos);
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
    public static final StimulusEvent<Place> PLACE = StimulusEvent.create(Place.class, ctx -> (player, world, pos, state, context) -> {
        try {
            for (Place listener : ctx.getListeners()) {
                ActionResult result = listener.onPlace(player, world, pos, state, context);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<AfterPlace> AFTER_PLACE = StimulusEvent.create(AfterPlace.class, ctx -> (player, world, pos, state) -> {
        try {
            for (AfterPlace listener : ctx.getListeners()) {
                listener.onPlace(player, world, pos, state);
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
    });

    public static final StimulusEvent<DropItems> DROP_ITEMS = StimulusEvent.create(DropItems.class, ctx -> (breaker, world, pos, state, dropStacks) -> {
        try {
            for (DropItems listener : ctx.getListeners()) {
                TypedActionResult<List<ItemStack>> result = listener.onDropItems(breaker, world, pos, state, dropStacks);
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

    public static final StimulusEvent<Use> USE = StimulusEvent.create(Use.class, ctx -> {
        return (player, hand, hitResult) -> {
            try {
                for (Use listener : ctx.getListeners()) {
                    ActionResult result = listener.onUse(player, hand, hitResult);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return ActionResult.PASS;
        };
    });

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
    public static final StimulusEvent<Punch> PUNCH = StimulusEvent.create(Punch.class, ctx -> {
        return (puncher, direction, pos) -> {
            try {
                for (Punch listener : ctx.getListeners()) {
                    ActionResult result = listener.onPunchBlock(puncher, direction, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return ActionResult.PASS;
        };
    });

    public interface Break {
        ActionResult onBreak(ServerPlayerEntity player, ServerWorld world, BlockPos pos);
    }

    public interface Place {
        ActionResult onPlace(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state, ItemUsageContext context);
    }

    public interface AfterPlace {
        void onPlace(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state);
    }

    public interface DropItems {
        TypedActionResult<List<ItemStack>> onDropItems(@Nullable Entity breaker, ServerWorld world, BlockPos pos, BlockState state, List<ItemStack> dropStacks);
    }

    public interface Use {
        ActionResult onUse(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult);
    }

    public interface Punch {
        ActionResult onPunchBlock(ServerPlayerEntity puncher, Direction direction, BlockPos pos);
    }
}
