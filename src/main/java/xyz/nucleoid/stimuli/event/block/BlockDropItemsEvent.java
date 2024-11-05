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
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link DroppedItemsResult#allow(List)} cancels further processing and drops the specified loot.
 * <li>{@link DroppedItemsResult#deny()} cancels further processing and drops no loot.
 * <li>{@link DroppedItemsResult#pass(List)} moves on to the next listener with the specified loot.</ul>
 *
 * The drop stacks list is not guaranteed to be mutable, so listeners modifying loot should first copy
 * the list before returning it in the result. If the drop stacks list is not modified, it can be passed
 * directly to the result.
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

    /**
     * @param dropStacks a list of dropped item stacks, which should be treated as immutable
     */
    DroppedItemsResult onDropItems(@Nullable Entity breaker, ServerWorld world, BlockPos pos, BlockState state, List<ItemStack> dropStacks);
}
