package xyz.nucleoid.stimuli.event.item;

import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to craft an item either in a crafting table or in their inventory.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the item to be crafted.
 * <li>{@link ActionResult#FAIL} cancels further processing and prevents the player from crafting.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the item can be crafted normally.
 */
public interface ItemCraftEvent {
    StimulusEvent<ItemCraftEvent> EVENT = StimulusEvent.create(ItemCraftEvent.class, ctx -> {
        return (player, recipe) -> {
            try {
                for (ItemCraftEvent listener : ctx.getListeners()) {
                    ActionResult result = listener.onCraft(player, recipe);
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

    ActionResult onCraft(ServerPlayerEntity player, Recipe<?> recipe);
}
