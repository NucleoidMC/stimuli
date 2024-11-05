package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.stimuli.event.DroppedItemsResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.List;

/**
 * Called when a {@link LivingEntity} drops its items on death.
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
public interface EntityDropItemsEvent {
    StimulusEvent<EntityDropItemsEvent> EVENT = StimulusEvent.create(EntityDropItemsEvent.class, ctx -> (dropper, items) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDropItems(dropper, items);

                // modify items from listener (some may want to pass while still modifying items)
                items = result.dropStacks();

                // cancel early if allow or deny was returned by the listener
                if (result.result() != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return DroppedItemsResult.pass(items);
    });

    /**
     * @param items a list of dropped item stacks, which should be treated as immutable
     */
    DroppedItemsResult onDropItems(LivingEntity dropper, List<ItemStack> items);
}
