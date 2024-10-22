package xyz.nucleoid.stimuli.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import java.util.List;

public record DroppedItemsResult(ActionResult result, List<ItemStack> dropStacks) {
    public static DroppedItemsResult success(List<ItemStack> dropStacks) {
        return new DroppedItemsResult(ActionResult.SUCCESS, dropStacks);
    }

    public static DroppedItemsResult fail(List<ItemStack> dropStacks) {
        return new DroppedItemsResult(ActionResult.FAIL, dropStacks);
    }

    public static DroppedItemsResult pass(List<ItemStack> dropStacks) {
        return new DroppedItemsResult(ActionResult.PASS, dropStacks);
    }
}