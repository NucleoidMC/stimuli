package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.DroppedItemsResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.List;

/**
 * Called when a block is broken and it tries to drop its items from a loot table.
 * <p>
 * Listeners can cancel item drops by returning a deny event result such as {@link DroppedItemsResult#deny(List)},
 * and can additionally modify the items dropped by modifying the returned {@link ItemStack} list.
 */
public interface BlockDropItemsEvent {
    StimulusEvent<BlockDropItemsEvent> EVENT = StimulusEvent.create(BlockDropItemsEvent.class, ctx -> (breaker, world, pos, state, dropStacks) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDropItems(breaker, world, pos, state, dropStacks);
                dropStacks = result.dropStacks();
                if (result.result() != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return DroppedItemsResult.pass(dropStacks);
    });

    DroppedItemsResult onDropItems(@Nullable Entity breaker, ServerWorld world, BlockPos pos, BlockState state, List<ItemStack> dropStacks);
}
