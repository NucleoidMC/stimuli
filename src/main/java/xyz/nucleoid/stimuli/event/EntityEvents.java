package xyz.nucleoid.stimuli.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import java.util.List;

public final class EntityEvents {
    /**
     * Called when a {@link LivingEntity} dies.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and kills the entity.
     * <li>{@link ActionResult#FAIL} cancels further processing and does not kill the entity.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the entity is killed.
     */
    public static final StimulusEvent<Death> DEATH = StimulusEvent.create(Death.class, ctx -> (entity, source) -> {
        try {
            for (Death listener : ctx.getListeners()) {
                ActionResult result = listener.onDeath(entity, source);
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
    public static final StimulusEvent<DropItems> DROP_ITEMS = StimulusEvent.create(DropItems.class, ctx -> (dropper, items) -> {
        try {
            for (DropItems listener : ctx.getListeners()) {
                TypedActionResult<List<ItemStack>> result = listener.onDropItems(dropper, items);

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

    public interface Death {
        ActionResult onDeath(LivingEntity entity, DamageSource source);
    }

    public interface DropItems {
        TypedActionResult<List<ItemStack>> onDropItems(LivingEntity dropper, List<ItemStack> items);
    }
}
