package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.List;

/**
 * Called when a {@link LivingEntity} drops its items on death.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and drops the current loot.
 * <li>{@link ActionResult#FAIL} cancels further processing and drops no loot.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * Listeners can modify the list of {@link ItemStack}s returned to them, regardless of what their result is.
 * If all listeners return {@link ActionResult#PASS}, the current loot is dropped.
 */
public interface EntityDropItemsEvent {
    StimulusEvent<EntityDropItemsEvent> EVENT = StimulusEvent.create(EntityDropItemsEvent.class, ctx -> (dropper, items) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDropItems(dropper, items);

                // modify items from listener (some may want to pass while still modifying items)
                items = result.getValue();

                // cancel early if success or fail was returned by the listener
                if (result.getResult() != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return TypedActionResult.pass(items);
    });

    TypedActionResult<List<ItemStack>> onDropItems(LivingEntity dropper, List<ItemStack> items);
}
