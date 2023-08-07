package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public class BlockUseEvent {
    /**
     * Called when a {@link ServerPlayerEntity} attempts to interact with a block.
     *
     * <p>This is before the game tries to use the block, or tries to use an item on the block.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the use.
     * <li>{@link ActionResult#FAIL} cancels further processing and cancels the use.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the use succeeds and proceeds with normal logic.
     */
    public static final StimulusEvent<Interact> INTERACT = StimulusEvent.create(Interact.class, ctx -> (player, hand, hitResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onBlockInteraction(player, hand, hitResult);
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
     * Called when a {@link ServerPlayerEntity} attempts to use a block.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the use.
     * <li>{@link ActionResult#FAIL} cancels further processing and cancels the use.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the use succeeds and proceeds with normal logic.
     */
    public static final StimulusEvent<Use> USE = StimulusEvent.create(Use.class, ctx -> (state, world, pos, player, hand, hit) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onBlockUse(state, world, pos, player, hand, hit);
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
     * Called when a {@link ServerPlayerEntity} attempts to use an item on a block.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the use.
     * <li>{@link ActionResult#FAIL} cancels further processing and cancels the use.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the use succeeds and proceeds with normal logic.
     */
    public static final StimulusEvent<UseItem> USE_ITEM = StimulusEvent.create(UseItem.class, ctx -> (stack, context) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onItemUseOnBlock(stack, context);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public interface Interact {
        ActionResult onBlockInteraction(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult);
    }

    public interface Use {
        ActionResult onBlockUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);
    }

    public interface UseItem {
        ActionResult onItemUseOnBlock(ItemStack stack, ItemUsageContext context);
    }

    /**
     * @deprecated Use {@link #INTERACT} instead.
     */
    @Deprecated
    public static final StimulusEvent<Interact> EVENT = INTERACT;
}
