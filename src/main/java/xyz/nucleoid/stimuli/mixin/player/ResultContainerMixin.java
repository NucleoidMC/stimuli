package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.item.ItemCraftEvent;

@Mixin(ResultContainer.class)
public abstract class ResultContainerMixin implements RecipeCraftingHolder {
    @Override
    public boolean setRecipeUsed(ServerPlayer player, RecipeHolder<?> recipe) {
        try (var invokers = Stimuli.select().forEntity(player)) {
            var result = invokers.get(ItemCraftEvent.EVENT).onCraft(player, recipe.value());
            if (result == EventResult.DENY) {
                return false;
            }
        }

		return RecipeCraftingHolder.super.setRecipeUsed(player, recipe);
    }
}
