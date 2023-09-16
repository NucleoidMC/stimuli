package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.item.ItemCraftEvent;

@Mixin(CraftingResultInventory.class)
public abstract class CraftingResultInventoryMixin implements RecipeUnlocker {
    @Override
    public boolean shouldCraftRecipe(World world, ServerPlayerEntity player, RecipeEntry<?> recipe) {
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(ItemCraftEvent.EVENT).onCraft(player, recipe.value());
            if (result == ActionResult.FAIL) {
                return false;
            }
        }

		return RecipeUnlocker.super.shouldCraftRecipe(world, player, recipe);
    }
}
