package xyz.nucleoid.stimuli.event;

import net.minecraft.item.ItemStack;

import java.util.List;

public record DroppedItemsResult(EventResult result, List<ItemStack> dropStacks) {
    public static DroppedItemsResult pass(List<ItemStack> dropStacks) {
        return new DroppedItemsResult(EventResult.PASS, dropStacks);
    }

    public static DroppedItemsResult allow(List<ItemStack> dropStacks) {
        return new DroppedItemsResult(EventResult.ALLOW, dropStacks);
    }

    public static DroppedItemsResult deny(List<ItemStack> dropStacks) {
        return new DroppedItemsResult(EventResult.DENY, dropStacks);
    }
}