package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link DispenserBlockEntity} is activated and is not empty.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the use.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the use.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the use succeeds and proceeds with normal logic.
 */
public interface DispenserUseEvent {
    StimulusEvent<DispenserUseEvent> EVENT = StimulusEvent.create(DispenserUseEvent.class, ctx -> (world, pos, blockPointerImpl, dispenserBlockEntity, slot, itemStack) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onUse(world, pos, blockPointerImpl, dispenserBlockEntity, slot, itemStack);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onUse(ServerWorld world, BlockPos pos, BlockPointerImpl blockPointerImpl, DispenserBlockEntity dispenserBlockEntity, int slot, ItemStack itemStack);
}
